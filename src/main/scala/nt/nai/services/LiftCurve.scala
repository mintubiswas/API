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




class LiftCurve extends Serializable {
  var counttt=0
  var countt =0
  val mlist = mutable.ArrayBuffer()
  var positive: Int= 0
  var negetive: Int = 0
  
    val array:Array[Int]= new Array[Int](10)
  val map = scala.collection.mutable.Map[Int,Int]()
  
  
    def LiftModelPackage(df: DataFrame, countDf:Long) : Unit = {
    
     var tdf=df
     println( "  prediction "   )
     var countPDf=tdf.count()
    // println(" count pdf  " +countPDf)
     val splits:Array[DataFrame] = tdf.randomSplit(Array(0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1))
   
     val dfs: DataFrame=null
    println(" from data set")
    val store:Array[DataFrame] = new Array[DataFrame](20)
    for(i <- 0 to 9)
    {
        store(i) = (splits(i))
      
    store(i)= store(i).groupBy("prediction").count()
      
      store(i).show()
      println( " group by prediction    "  )
      
    }
    store(10)=store(0)
    for(i <- 1 to 9)
    {
       store(10)=store(10).union(store(i))
    }
    
    println(" final  ")
    store(10).show()
    println(" array of dataframe  ")
   
    
    var obj=new LiftRest
    obj.liftAndGain(store(10), countPDf)
  }
  
  
}