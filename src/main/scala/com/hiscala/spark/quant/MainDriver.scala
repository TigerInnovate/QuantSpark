package com.hiscala.spark.quant

/**
  * Created by Guanjun.Wang on 2016/1/23.
  */

import com.hiscala.quant.trading.YahooFinancials
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
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

    val volatilityVolume: RDD[(Double, Double)] = parsedSymbolTxn.map(x => YahooFinancials.volatilityVol(x.metrics))
    import com.hiscala.spark.stats.Stats
    val normalizedVolatility: RDD[Double] = new Stats(volatilityVolume.map(_._1)).normalize
    val normalizedVolume = new Stats(volatilityVolume.map(_._2)).normalize

    val normalizedVolaVolu: RDD[(Double, Double)] = normalizedVolatility zip normalizedVolume

    normalizedVolaVolu.take(10).foreach(println)


    //1. label training dataset

    val lblPnts: RDD[LabeledPoint] = normalizedVolaVolu.map((vv: (Double, Double)) => {
      if (vv._1 > 0.3 && vv._2 > 0.1)
        LabeledPoint(1.0, Vectors.dense(vv._1, vv._2))
      else
        LabeledPoint(0.0, Vectors.dense(vv._1, vv._2))
    })

    val splits = lblPnts.randomSplit(Array(0.6, 0.4), seed = 11L)
    val trainingSet = splits(0).cache()
    val testSet = splits(1)

    //2. train the model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(10)
      .run(trainingSet)

    //3. predict
    val predictionAndLabels = testSet.map { case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)
    }

    // Get evaluation metrics.
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val precision = metrics.precision
    println("Precision = " + precision)
    println("Recall = " + metrics.recall)

    /*// Save and load model
    model.save(sc, "myModelPath")
    val sameModel = LogisticRegressionModel.load(sc, "myModelPath")
    */
  }
}
