package com.hiscala.spark.quant

/**
  * Created by Guanjun.Wang on 2016/1/23.
  */

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object MainDriver extends Serializable {

  def workingDirectory = new java.io.File(".").getCanonicalPath

  def main(args: Array[String]) = {
    val masterUrl = args(0)
    val sc = new SparkContext(new SparkConf().setAppName("Quant Prototype").setMaster(masterUrl))

    println("working Directory is " + workingDirectory)

    val symbol = "CSCO"
    val path = "./data/symbols"
    val filename = "csco.csv"
    val filePath = s"$path/$filename"

    val parsedSymbolTxn: RDD[SymbolTransaction] = new QuantSymbolLoader(symbol, sc).loadCSV(filePath)

    //parsedSymbolTxn.take(100).foreach(println)

    //check stats
    val nums = Stats.numFieldsStats(parsedSymbolTxn.map(r => r.metrics))
    val dts = Stats.dateFieldStats(parsedSymbolTxn.map(r => r.date))

    println("---print number stats---")
    nums.foreach(println)
    println("---print date stats---")
    println(dts)

    //run logistic regression

    //1. label training dataset


    //2. train the model

    //3. predict

  }
}
