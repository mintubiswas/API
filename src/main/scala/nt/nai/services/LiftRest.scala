package nt.nai.services
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashMap
import collection.mutable 
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.spark.ml.classification.RandomForestClassificationModel
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.evaluation.Evaluator
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.tuning.ParamGridBuilder
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

import nt.nai.ml.core.sink.ElasticSearchSinkHelper
import nt.nai.ml.model.attr.RandomForestAttributes
import nt.nai.ml.util._
import nt.nai.ml.util.CommonProperties
import nt.nai.ml.util.CommonPropertiesBranchWise
import nt.nai.ml.util.CommonUtilBranchWise
import nt.nai.ml.util.HyperParameterTunningProperties
import nt.nai.ml.util.RandomForestRegressionProperties
import org.datavec.spark.transform.DataFrames
import org.apache.spark.sql.Dataset


class LiftRest {
  def liftAndGain(df:DataFrame,DfCount:Long){
    
    /*var sparkSession = SparkSession.builder.master("local").appName("Lift And Gain").getOrCreate()
    var sc = sparkSession.sparkContext
    val sqlContext = new SQLContext(sc)*/
    var LiftMap = scala.collection.mutable.Map[Any,Array[Array[Double]]]()
    var GainMap = scala.collection.mutable.Map[Any,Array[Array[Double]]]()
    var dsLabelCount = df.select("prediction").distinct().count
   
    var DfLabel: DataFrame = df.select("prediction").distinct()
    
    var list = DfLabel.select("prediction").rdd.map(r => r(0)).collect()
    var colsName=List("LabelValue","Population","Response Before Prediction","Response After Predictive","Gain","Lift")
    
    for (i <- 0 to (list.length-1)) {
      var RB = (DfCount / 10).toInt
      //val ds: DataSet = null
      var rbArray: Array[Int] = new Array[Int](20)
      var prArray: Array[Int] = new Array[Int](20)
      var PopArray: Array[Int]= new Array[Int](20)
      var Lifttable=Array.ofDim[Double](10,10)
      var Gaintable=Array.ofDim[Double](10,10)
      var label=list(i)
      var outDf:DataFrame = df.filter(df.col("prediction") === list(i)).sort(desc("count")).toDF()
      outDf=outDf.drop("prediction")
     var Sortlist = outDf.select("count").rdd.map(r => r(0)).collect()
  
     var Intlist=Sortlist.map(_.toString.toInt)
    
      for (j <- 0 to (Intlist.length-1)) {
        var gain: Double = 0
        var lift: Double = 0
        if (j == 0) {
          rbArray(j) = (RB/10).toInt
          PopArray(j)=RB
          prArray(j) = Intlist(j)
       
          gain = (prArray(j) / rbArray(j)).toDouble
          lift = (Intlist(j) / RB).toDouble
        } else {
          rbArray(j) = ((RB/10).toInt) * (j + 1)
          PopArray(j)= RB * (j + 1)
          prArray(j) = prArray(j-1)+Intlist(j)
          
          gain = (prArray(j) / rbArray(j)).toDouble
          lift = (Intlist(j) / RB).toDouble
        }
        
        Lifttable(j)(0)=PopArray(j)
        Gaintable(j)(0)=PopArray(j)
       // table(j)(2)=rbArray(j)
      //  table(j)(3)=prArray(j)
        Lifttable(j)(1)=gain
        Gaintable(j)(2)=lift
      }
     LiftMap.put(list(i), Lifttable)
     GainMap.put(list(i), Gaintable)
    }
    /*
    for ((k,v) <- LiftMap) println(s"key: $k, value: $v")
      LiftMap.foreach(println)
      println(" LiftMap  ")
      GainMap.foreach(println)
      println("    GainMap   ")*/
   
  }
  
}