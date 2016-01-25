/**
  * Copyright (c) 2016 Guanjun Wang - Scala Library for Quant Analysis - All rights reserved
  *
  * Visit http://www.hiscala.com for more information.
  *
  * Version 0.1
  */
package com.hiscala.spark.stats

import org.apache.spark.rdd.RDD

/**
  * Parameterized class that computes and update the statistics
  * (mean, standard deviation) for any set of observations for which the
  * type can be converted to a Double.
  *
  * This class is immutable as no elements can be added to the original set of values.
  * @constructor Create an immutable statistics instance for a vector of type T
  * @see com.hiscala.spark.quant.Symbol
  *
  * @author Guanjun Wang
  * @since 2016/1/25
  * @version 0.0.1
  * @note Scala Library for Quant Analysis
  */
class Stats(val variable: RDD[Double]) {

  private val _stats: NAStatCounter = NAStatCounter.variableStats(variable)

  def count = _stats.stats.count

  def sum = _stats.stats.sum

  def mean = _stats.stats.mean

  def min = _stats.stats.min

  def max = _stats.stats.max

  def variance = _stats.stats.variance

  def stdDev = _stats.stats.stdev

  def missing = _stats.missing

  override def toString = _stats.toString

  def normalize = {
    val _range = max - min
    val _min = min //use local variable to avoid serializable
    variable.map(x => (x - _min) / _range)
  }
}

//-------------------EOF-------------------