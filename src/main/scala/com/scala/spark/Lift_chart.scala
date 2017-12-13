package com.scala.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.SparkContext
class Lift_chart extends Serializable {
  
  
  def LiftModelPackage: Unit = {
    var sparkSession = SparkSession.builder.master("local").appName(" Lift Chart").getOrCreate()
    var sc=sparkSession.sparkContext
    val df = sparkSession.read.option("header", "true").csv("hdfs://localhost:8020/data/lift/lift.csv")
    df.show()
    var con = df.collect.map(_.toSeq.map(_.toString))
   // con.foreach {keyVal => println(keyVal._1 + "=" + keyVal._2)}
        var conH = df.columns
    
        
  }
  
}