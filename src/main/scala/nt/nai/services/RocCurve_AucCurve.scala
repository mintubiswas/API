package nt.nai.services
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashMap
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.ml.linalg.Vectors
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
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
//import com.collective.TestSparkContext
import org.apache.spark.rdd.RDD
import org.scalatest.{ GivenWhenThen, FlatSpec }

class RocCurve_AucCurve {

  def roc(df: DataFrame, Lcol: String): Unit = {
    val binaryLabel: Boolean = true

    val dfs = df.withColumn("label", (df.col(Lcol)).cast(DoubleType))
      .drop(df.col(Lcol))
      .withColumnRenamed("label", Lcol)

    var metricsToFindArea = new BinaryClassificationMetrics(dfs
      .select(col("probability"), col(Lcol))
      .rdd.map(r => (r.getAs[DenseVector](0)(1), r.getDouble(1))))

    var rocCv = metricsToFindArea.roc().collect()
    println(" roc points  ")
    rocCv.foreach(println)

    var precision = metricsToFindArea.precisionByThreshold()
    var areaUnderRocval = metricsToFindArea.areaUnderROC()
    var areaunderPRval = metricsToFindArea.areaUnderPR()

    /*  println(" areaUnderRocval  " +areaUnderRocval)
    println(  " areaunderPRval " +areaunderPRval )*/

    var sparkSession = SparkSession.builder.master("local").appName("Als Recommender").getOrCreate()
    var sc = sparkSession.sparkContext
    val sqlContext = new SQLContext(sc)
    val outputStorageDir = "hdfs://localhost:8020/AlsOutput/roc"

    val conf = new Configuration()
    conf.set("fs.defaultFS", "hdfs://localhost:8020")
    val fs = FileSystem.get(conf)
    checkExistsAndDelete(outputStorageDir, fs)

  }
  def checkExistsAndDelete(fileFullPath: String, fileSystem: FileSystem): Unit =
    {
      println("check if file Exists on hdfs And if it does then delete output dir on hdfs")
      val path: Path = new Path(fileFullPath)
      if (fileSystem.exists(path)) {
        fileSystem.delete(path, true);
      } else {
        println("output directory does not exists")
      }
      return
    }
}