package nt.nai.services
 import org.apache.spark.streaming._
import org.apache.spark.streaming.scheduler.StreamingListener
import org.apache.spark.streaming.scheduler.StreamingListenerBatchCompleted
import org.apache.spark.sql.SparkSession

object HeatMap {
  def main(args: Array[String])
  {
   
case class Loc(lat: Double, lon: Double)

case class Tweet(timestamp: java.util.Date, user: String, tweet: String, loc: Loc)

 var sparkSession = SparkSession.builder.master("local").appName("Als Recommender").getOrCreate()
    println("in read data")
    var sc=sparkSession.sparkContext
val input = sparkSession.read.option("header","true").option("inferSchema", true).csv("hdfs://localhost:8020/data/lift2/credit_defaults.csv")

/*input.foreachRDD(rdd => {
    val df = rdd
      .filter(_.getGeoLocation() != null)
      .map(s => Tweet(
          s.getCreatedAt,
          s.getUser.getName,
          s.getText,
          Loc(s.getGeoLocation.getLatitude, s.getGeoLocation.getLongitude)))
    
    var items = df.collect*/
    
 

//})

  }
}