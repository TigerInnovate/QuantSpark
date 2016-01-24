/**
  * Copyright (c) 2016 Guanjun Wang - Scala Library for Quant Analysis - All rights reserved
  *
  * Visit http://www.hiscala.com for more infomation.
  *
  * Version 0.1
  */
package com.hiscala.quant.core

/**
  * Package that encapsulates the types and conversion used in Scala for Quant Analysis.
  * Internal types conversion are defined by the Primitives singleton. The type conversion
  * related to each specific libraries are defined by their respective singleton
  * (i.e CommonsMath).
  *
  * @author Guanjun Wang
  * @since 2016/1/24
  * @version 0.0.1
  * @note Scala Library for Quant Analysis
  */
object Types {
  val emptyString = ""

  type Fields = Array[String]
  type DblArray = Array[Double]
}

//-------------------EOF-------------------