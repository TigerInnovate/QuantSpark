/**
  * Copyright (c) 2016 Guanjun Wang - Scala Library for Quant Analysis - All rights reserved
  *
  * Visit http://www.hiscala.com for more infomation.
  *
  * Version 0.1
  */
package com.hiscala.quant.trading

import com.hiscala.quant.core.Types.{DblArray, Fields}

/**
  * Enumerator that describes the fields used in the extraction of price related data from
  * the Yahoo finances historical data. The data is loaded from a CSV file.
  *
  * @author Guanjun Wang
  * @since 2016/1/24
  * @version 0.0.1
  * @note Scala Library for Quant Analysis
  */

object YahooFinancials extends Enumeration {
  type YahooFinancials = Value

  //variables
  //val DATE, OPEN, HIGH, LOW, CLOSE, VOLUME, ADJ_CLOSE = Value
  val OPEN, HIGH, LOW, CLOSE, VOLUME, ADJ_CLOSE = Value

  private final val EPS = 1e-6

  /**
    * Convert a variable to a Double
    * @param v the value
    * @return a Double, given the variable name
    */
  def toDouble(v: Value): Fields => Double = (s: Fields) => s(v.id).toDouble

  /**
    * Convert variables to an array of Double
    * @param vs
    * @return
    */
  def toDoubleArray(vs: Array[Value]): Fields => DblArray =
    (s: Fields) => vs.map(v => s(v.id).toDouble)

  /**
    * Calculate the ratio of two variables
    * @param v1 the first variable
    * @param v2 the second variable
    * @return (v1 - v2) / v2
    */
  def ratio(v1: Value, v2: Value): Fields => Double = (s: Fields) => {
    val den = s(v2.id).toDouble
    if( den < EPS) -1.0
    else  s(v1.id).toDouble/den - 1.0
  }

  /**
    * Derive variable: the volatility of variable HIGH, LOW
    * Calculus: ratio(HIGH, LOW)
    */
  val volatility = ratio(HIGH, LOW)

  /**
    * Derive variable: Compute the volatility over volume
    */
  val volatilityVol = (s: Fields) =>
    (s(HIGH.id).toDouble - s(LOW.id).toDouble, s(VOLUME.id).toDouble)
}

//-------------------EOF-------------------