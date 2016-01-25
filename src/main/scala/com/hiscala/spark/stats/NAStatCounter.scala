/**
  * Copyright (c) 2016 Guanjun Wang - Scala Library for Quant Analysis - All rights reserved
  *
  * Visit http://www.hiscala.com for more information.
  *
  * Version 0.1
  */
package com.hiscala.spark.stats

import org.apache.spark.rdd.RDD
import org.apache.spark.util.StatCounter

/**
  * description of this file...
  * @constructor Create an instance of .... 
  * @see com.hiscala.spark.quant.Symbol
  *
  * @author Guanjun Wang
  * @since 2016/1/25
  * @version 0.0.1
  * @note Scala Library for Quant Analysis
  */
class NAStatCounter extends Serializable {
  val stats: StatCounter = new StatCounter()
  var missing: Long = 0

  def add(x: Double): NAStatCounter = {
    if (x.isNaN) {
      missing += 1
    } else {
      stats.merge(x)
    }
    this
  }

  def merge(other: NAStatCounter): NAStatCounter = {
    stats.merge(other.stats)
    missing += other.missing
    this
  }

  override def toString: String = {
    "stats: " + stats.toString + " NaN: " + missing
  }
}

object NAStatCounter extends Serializable {
  def apply(x: Double) = new NAStatCounter().add(x)

  def numFieldsStats(rdd: RDD[Array[Double]]): Array[NAStatCounter] = {
    val nastats = rdd.mapPartitions((iter: Iterator[Array[Double]]) => {
      val nas: Array[NAStatCounter] = iter.next().map(d => NAStatCounter(d))
      iter.foreach(arr => {
        nas.zip(arr).foreach { case (n, d) => n.add(d) }
      })
      Iterator(nas)
    })
    nastats.reduce((n1, n2) => {
      n1.zip(n2).map { case (a, b) => a.merge(b) }
    })
  }

  def variableStats(rdd: RDD[Double]): NAStatCounter = {
    val nastats: RDD[NAStatCounter] = rdd.mapPartitions((iter: Iterator[Double]) => {
      val na: NAStatCounter = NAStatCounter(iter.next())
      iter.foreach( a => {
        na.add(a)
      })
      Iterator(na)
    })

    nastats.reduce((n1, n2) => n1.merge(n2))
  }
}

//-------------------EOF-------------------