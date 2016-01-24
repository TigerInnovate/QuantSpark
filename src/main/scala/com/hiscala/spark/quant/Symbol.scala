package com.hiscala.spark.quant

import org.joda.time.DateTime

/**
  * Created by Administrator on 2016/1/23.
  */
case class Symbol(val symbolName: String)

case class SymbolTransaction(val symbolName: String, val date: DateTime, val metrics: Array[Double]){

  override def toString = s"${symbolName} | ${date} | ${metrics.mkString(",\t\t")}"
}
