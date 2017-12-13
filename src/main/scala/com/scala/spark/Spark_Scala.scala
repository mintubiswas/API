package com.scala.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.SparkContext
object Spark_Scala {
  def main(args: Array[String])
  {
    var spark = SparkSession.builder.master("local").appName("Spark Test").getOrCreate()
    var sc = spark.sparkContext
    val df=spark.read.option("header","true").csv("hdfs://localhost:8020/data/baby/baby.csv")
    var rddf= df.rdd
    
    println(" baby count is   " +rddf.count())
    println(" baby tables columns  " +rddf.first)
    
  //  val rows = rddf.map(line => line.split(","))
  }
}