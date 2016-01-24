package com.hiscala.spark.quant

import org.apache.spark.rdd.RDD
import org.apache.spark.util.StatCounter
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import com.github.nscala_time.time.Imports._


/**
  * Created by Guanjun.Wang on 2016/1/23.
  */
object Stats {

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

  def dateFieldStats(rdd: RDD[DateTime]): (Int, DateTime, DateTime) = {
    //(cnt, min, max)
    val leastDate = DateTime.parse("1900-01-01", ISODateTimeFormat.date())
    val greatestDate = DateTime.parse("2099-01-01", ISODateTimeFormat.date())

    rdd.aggregate((0, greatestDate, leastDate))(
      (acc, value) => (acc._1 + 1, if (value < acc._2) value else acc._2, if (value > acc._3) value else acc._3),
      (acc1, acc2) => (acc1._1 + acc2._1, if (acc1._2 < acc2._2) acc1._2 else acc2._2, if (acc1._3 > acc2._3) acc1._3 else acc2._3))
  }

}


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
}