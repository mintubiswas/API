package com.scala.spark

import org.json.simple.JSONObject
import org.json.simple.JSONArray
import org.apache.spark.mllib.recommendation._
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.json.simple.parser.JSONParser
import scala.collection.mutable.HashMap
import org.apache.spark.sql.types._
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.ml.tuning.{ ParamGridBuilder, TrainValidationSplit }
import org.apache.spark.mllib.evaluation.{ RegressionMetrics, RankingMetrics }
//import org.apache.spark.mllib.evaluation.RegressionMetrics
import java.net.URI
import scala.collection.mutable.Map
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.linalg.{ Vector, VectorUDT }
import scala.collection.mutable.LinkedHashMap
import org.apache.spark.mllib.evaluation.RegressionMetrics
import scala.collection.mutable.ArrayBuffer
import java.util.Arrays
import java.io.File
import org.apache.spark.sql.types.DoubleType

object recomender {
  def main(args: Array[String])
  {
    println(" AlsMl Product ")
   var alsml = new AlsMlProduct()
   alsml.alsModelingPackage()
  // alsml.writeToHDFS(df, "true", "/AlsOutput/output")
   println(" AlsMl object Created")
  }
}