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

class liftF2class {
  def modelClass(df:DataFrame){
    var commonLabelCol:String ="default_payment_next_month"
    var lCols:String="probability"
    
    var fd:DataFrame= df.groupBy(commonLabelCol).count.sort(commonLabelCol)
    fd.show
    println( " fd values LiftFclass" )
    var LabelList= fd.select(commonLabelCol).rdd.map(r => r(0)).collect()
    LabelList.foreach(println)
    println(" LabelList values LiftFclass")
    val disassembler = new org.apache.spark.ml.feature.VectorDisassembler()
      .setInputCol(lCols)
    var finalDf:DataFrame=disassembler.transform(df)
    var LiftMatrix1:Array[Double]=new Array[Double](10)
    var LiftMatrix2:Array[Double]=new Array[Double](10)
    var GainMatrix1:Array[Double]=new Array[Double](10)
    var GainMatrix2:Array[Double]=new Array[Double](10)
    for(i <- 0 to (LabelList.length-1))
    {
      var truePositive:Array[Int]=new Array[Int](10)
     var label:String=lCols +"_" + i.toString()
    // var finalDff=finalDf.orderBy(desc(label))
     finalDf=finalDf.select(commonLabelCol,label,"prediction")
     val splits:Array[DataFrame] = finalDf.randomSplit(Array(0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1))
      val store:Array[DataFrame] = new Array[DataFrame](2)
     for(j <- 0 to 9)
     {
       var colName=LabelList(i).toString()
       var predictedDf=splits(j)
       var confusionMatrixBinary =predictedDf.stat.crosstab("prediction", commonLabelCol)
       var commonLabelColnew="prediction"+"_"+commonLabelCol
       var outDf:DataFrame = confusionMatrixBinary.filter(confusionMatrixBinary.col(commonLabelColnew) === LabelList(i)).toDF()
       outDf.show()
       println(" oufDFFF ")
       var Sortlist = outDf.select(i.toString).rdd.map(r => r(0)).collect().map(_.toString.toInt)
       Sortlist.foreach(println)
       println(" Sortlist length   "+Sortlist.length)
       truePositive(j)=Sortlist(0)
     }
     val sumOfValues=truePositive.sum
     println(" sumvalue is  " +sumOfValues)
     var gain:Double=0
     var lift:Double=0
     var response:Double=0
     truePositive=truePositive.sortWith(_ > _)
     truePositive.foreach(println)
     println(" sorted array ")
     for(k <- 0 to (truePositive.length-1))
     {
       response=response+truePositive(k).toDouble
       println("response is "+response)
       gain=(response/(sumOfValues.toDouble)).toDouble
       gain=(gain*100).toDouble
      // println(" gain is " +gain)
       var divide:Double=((k+1)*10).toDouble
       lift=(gain/divide)
       // println(" lift is " +lift)
       GainMatrix1(k)=gain
       LiftMatrix1(k)=lift
     }
     GainMatrix1.foreach(println)
     println(" GainMatrix1 ")
     LiftMatrix1.foreach(println)
     println(" LiftMatrix1 ")
     
    }
   
    
  }

}