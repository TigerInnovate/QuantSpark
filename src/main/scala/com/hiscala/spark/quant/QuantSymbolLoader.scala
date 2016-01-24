package com.hiscala.spark.quant

import java.text.SimpleDateFormat

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime

/**
  * Created by Guanjun.Wang on 2016/1/23.
  */
class QuantSymbolLoader(val symbol: String, val sc: SparkContext) {

  def loadCSV(filePath: String) = load(filePath, ",")

  def load(filePath: String, separator: String) = {
    val _symbol = symbol
    val rawBlocks: RDD[String] = sc.textFile(filePath)
    val noheader = rawBlocks.filter(!_.contains("Date"))

    val parsedTrxn = noheader
      .map(l => QuantSymbolLoader.parse(l, separator))
      .map(t => SymbolTransaction(_symbol, t._1, t._2))

    parsedTrxn
  }
}

object QuantSymbolLoader {

  def toDate(dateStr: String, format: String) = {
    val f = new SimpleDateFormat(format)
    val date = f.parse(dateStr)
    new DateTime(date)
  }

  def parse(l: String, separator: String) = {
    val row = l.split(separator)
    val txnDt = toDate(row(0), "yyyy-MM-dd")
    val mtx = row.slice(1, 8).map(_.toDouble)
    (txnDt, mtx)
  }
}
