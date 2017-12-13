package nt.nai.services

import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.sql.DataFrame
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object RocTest {
  def main(args: Array[String])
  {
    var sparkSession = SparkSession.builder.master("local").appName("Als Recommender").getOrCreate()
    println("in read data")
    var sc=sparkSession.sparkContext
    val data= MLUtils.loadLibSVMFile(sc, "/home/mintu-biswas/Downloads/credit_defaults.csv")
  
    // Split data into training (60%) and test (40%)
    
    val Array(training, test) =data.randomSplit(Array(0.6,0.4), seed=11L)
    training.cache()
    
    // Run training algorithm to build the model
    
    val model = new LogisticRegressionWithLBFGS()
    .setNumClasses(2)
    .run(training)
    
    // Clear the prediction threshold so the model will return probabilities
    
    model.clearThreshold()
    
    // Compute raw scores on the test set
   // var label="default_payment_next_month"
   // label.toDouble
    var features = Vector("ID", "LIMIT_BAL", "EDUCATION", "MARRIAGE", "AGE", "BILL_AMT1", "BILL_AMT2", "BILL_AMT3", "BILL_AMT4", "BILL_AMT5", "PAY_AMT1" ,"PAY_AMT2" ,"PAY_AMT4" ,"default_payment_next_month")
    features.toVector
    val predictionAndlabels = test.map {case LabeledPoint(label,features) =>
     // label=("default_payment_next_month").toDouble
       val prediction = model.predict(features)
       (prediction,label)
    }
      
    // Instantiate metrics object
    
     val metrics = new BinaryClassificationMetrics(predictionAndlabels) 
    
     // Precision by threshold
     
      val precision = metrics.precisionByThreshold
        precision.foreach{case (t,p) =>
          println(s"Threshold: $t, Precision: $p")
        }
    
     // Recall by threshold
     
     val recall = metrics.recallByThreshold
         recall.foreach{ case (t,r) => 
           println(s"Threshold: $t, Recall: $r")
           }
     
     // Precision-Recall Curve
     val PRC = metrics.pr
     
     // F-measure
     
     val f1Score = metrics.fMeasureByThreshold()
       f1Score.foreach{ case (t,f) =>
         println(s"Threshold: $t, F-score: $f, Beta = 1")
       }
     
    val beta = 0.5
    val fScore = metrics.fMeasureByThreshold(beta)
      f1Score.foreach{ case (t,f) =>
        println(s"Threshold: $t, F-score: $f, Beta = 0.5")
      }
    
    // AUPRC
    val auPRC = metrics.areaUnderPR()
    println("Area under precision-recall curve = " +auPRC)
    
    // Compute thresholds used in ROC and PR curves
    
    val thresholds = precision.map(_._1)
    
    // ROC Curve
    
    val roc = metrics.roc()
    
    // AUROC
    
    val auROC = metrics.areaUnderROC()
    println("Area under ROC = " + auROC)
    
  }
}