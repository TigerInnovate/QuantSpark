package com.hiscala.spark.quant

import com.hiscala.quant.core.Types.Fields
import org.joda.time.DateTime

/**
  * Created by Administrator on 2016/1/23.
  */
case class Symbol(val symbolName: String)

case class SymbolTransaction(val symbolName: String, val date: DateTime, val metrics: Fields) {

  override def toString = s"${symbolName} | ${date} | ${metrics.mkString(",\t\t")}"
}
