/*package com.scala.spark
package nt.nai.ml.model.dev

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{ SparkSession, DataFrame }
import org.apache.spark.ml.classification.RandomForestClassifier
//import nt.nai.ml.util.{ CommonProperties, CommonPropertiesBranchWise, RandomForestRegressionProperties, CommonUtilBranchWise }
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import scala.collection.mutable.ArrayBuffer
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import java.net.URI
//import nt.nai.ml.util._
import org.apache.spark.ml.regression.RandomForestRegressionModel
//import nt.nai.ml.util.HyperParameterTunningProperties
import org.apache.spark.ml.classification.RandomForestClassificationModel
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.ml.tuning.ParamGridBuilder
import org.apache.spark.ml.evaluation.{ Evaluator, BinaryClassificationEvaluator, MulticlassClassificationEvaluator }
import org.apache.spark.mllib.evaluation.{ BinaryClassificationMetrics, MulticlassMetrics }
import org.apache.spark.ml.feature.VectorAssembler
//import nt.nai.ml.model.attr.RandomForestAttributes
//import nt.util.MySQLUtil
//import nt.main.MySQLConnectivity
import scala.collection.mutable.HashMap
import scala.collection.mutable.LinkedHashMap
import org.apache.spark.sql.functions._
//import nt.nai.ml.core.sink.ElasticSearchSinkHelper
class RandomForestClassificationMl() {

  *//**
   *
   * Global variables
   *//*
  var hadoopConf: Configuration = null
  var fileSystem: FileSystem = null
  var dfCount: Int = 0
  var hiveListOfCols: ArrayBuffer[String] = new ArrayBuffer()
  var appName: String = "RandomForestClasssification"
  var transformedDF : DataFrame= null
  var randomForestClassificationModelHashMap: LinkedHashMap[String, RandomForestClassificationModel] = new LinkedHashMap[String, RandomForestClassificationModel]()
  var randomForestClassificationMetricHashMap: LinkedHashMap[String, LinkedHashMap[String, Any]] = new LinkedHashMap[String, LinkedHashMap[String, Any]]()
  var accuracyVal = 0.0
  var areaUnderRocval = 0.0
  var areaunderPRval = 0.0
  var precisionVal = 0.0
  var recallVal = 0.0
  var fmeasureVal = 0.0
  var DataHeaderPresent="true"
  var DataSource="hdfs"
  *//**
   *
   * Model for Random Forest Regression
   *
   *//*

  *//**
   * RandomForestRegressor properties
   * LabelCol,FeaturesCol,Impurity,MaxBins,MaxDepth,MinInfogain,numtrees,Seed,PredictionCol,CacheNodeIds,checkPointInterbal,FeatureSubsetStrategy
   * maxmemoryInMB,MinInstancesperNode,Probability,RawPredictionCOl,Thresholds
   *//*

  def randomForestClassificationModelPackage(inputDF: DataFrame): (DataFrame, RandomForestClassificationModel) = {
    var df = inputDF
    var rowsIndf = df.count()
   println("myRF DFcount"+rowsIndf)
    var model: RandomForestClassificationModel = null
  //  var outpath = "/" + userId + "/" + pipeLineId + "/" + branchId + "/" + Constants.RANDOMFORESTCLASSIFICATION
    var commonLabelCol: String = ""
    println(commonLabelCol + "-----commonCol-----------------------")
   // println(CommonPropertiesBranchWise(userId, pipeLineId, branchId).getCommonLabelCol() + "--------------from common property ")
    var commonFeatureCols: Array[String] = null

    *//**
     * PipeLine Changes
     *
     *//*
    try {
      //new changes 
     // var pipelineON = CommonProperties.getPipeLineOn()
      var featureColNames: Seq[String] = Nil

      *//**
       *
       * check for pipeline
       *
       *//*
      var isPipeline = "false"

      *//**
       *
       * Null check for isPipeline
       *
       *//*
      if (isPipeline == null ) {

        *//**
         *
         * setting default value to pipeline
         *//*
        //isPipeline = "false"

      }

      if (isPipeline.equalsIgnoreCase("true")) {
        *//**
         *
         * DataSource and
         * Header check
         *
         *//*
        if (DataSource.equalsIgnoreCase("hdfs") && DataHeaderPresent.equalsIgnoreCase("false")) {
          *//**
           * update commonLabelColumn value
           *  not set directly in CommonPropertires
           *  because in CommonUtilBranchWise(userId,pipeLineId,branchId) derviceFeatureColumnsInPipeline check for header
           *
           *//*

          var colNo: String = (commonLabelCol.toInt - 1).toString
          commonLabelCol = "_c" + colNo

        } //header and datasource check end here

        val (newDf, cols) = df
        df = newDf
        commonFeatureCols = cols
        //      CommonPropertiesBranchWise(userId,pipeLineId,branchId).setCommonLabelCol(commonLabelCol)

      } //pipeline true
      else {

        *//**
         *
         * isPipeline false
         *
         *//*

        *//**
         * Drop non features column if any
         *
         *//*

        if (CommonPropertiesBranchWise(userId, pipeLineId, branchId).getNonFeaturesColsList() != null && !CommonPropertiesBranchWise(userId, pipeLineId, branchId).getNonFeaturesColsList().isEmpty()) {
          var dropColArray: Array[String] = CommonPropertiesBranchWise(userId, pipeLineId, branchId).getNonFeaturesColsList().split(",")
          if (CommonProperties.getDataSource().equalsIgnoreCase("HDFS") && CommonProperties.getDataHeaderPresent().equalsIgnoreCase("false")) {
            for (i <- 0 until dropColArray.size) {
              var newValue: String = "_c" + (dropColArray(i).toInt - 1)
              dropColArray.update(i, newValue) //debug

            }
          } //header check end here
          df = CommonUtilBranchWise(userId, pipeLineId, branchId).deleteColumnsFromDataFrame(df, dropColArray)

        }

        if (CommonProperties.getDataSource().equalsIgnoreCase("HDFS") && CommonProperties.getDataHeaderPresent().equalsIgnoreCase("false")) {
          var colNo: String = (commonLabelCol.toInt - 1).toString
          commonLabelCol = "_c" + colNo
        }
        var allColsOfDFarray = df.columns.toArray
        commonFeatureCols = allColsOfDFarray.filterNot(elm => elm == commonLabelCol)

      } //else of pipeline 

      //typeCast columns to Double 
      //check for multiClass
      //    var evaluator:Any = null
      var evaluator: Evaluator = null
      var accuracy: Double = 0.0
      if (RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfIsMultiClass()) {
        println("in multiClass" + RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfIsMultiClass())
        evaluator = new MulticlassClassificationEvaluator()
          .setLabelCol(commonLabelCol)
          .setPredictionCol("prediction")
          .setMetricName(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMetricName())

      } else {
        println("in binary" + RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfIsMultiClass())
        evaluator = new BinaryClassificationEvaluator()
          .setLabelCol(commonLabelCol)
          .setMetricName(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMetricName())
      }

//      if (CommonProperties.getDataSource().equalsIgnoreCase("HDFS")) {
//        //typeCast
//        val toDouble = udf[Double, String](_.toDouble)
//        df.show()
//        println("cfcs " + commonFeatureCols.mkString("||"))
//        if (commonFeatureCols != null) {
//
//          for (i <- 0 until commonFeatureCols.size) {
//            df = df.withColumn(commonFeatureCols(i), toDouble(df.col(commonFeatureCols(i))))
//          }
//        }
//        df = df.withColumn(commonLabelCol, toDouble(df.col(commonLabelCol)))
//      }

      // labelcol of Double type and FeaturesCol of Vector
      //output columns are predictionCol (Double type),rawPredictionCol(Vector),probabilityCol(vector),varianceCol(Double)
      var resultHasCol = CommonUtil.hasColumn(df, "features")
      if (!resultHasCol) {
      val vectorassembler = new VectorAssembler()
        .setInputCols(commonFeatureCols)
        .setOutputCol("features")

      transformedDF = vectorassembler.transform(df)
      transformedDF.show()
      }else {
        transformedDF = df
      }

      println("adad")
      var predictedDF: DataFrame = null
      val splits = transformedDF.randomSplit(Array(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfTrainPercentage(), RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfTestPercentage()))
      println("train test % RF " + RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfTrainPercentage() + " " + RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfTestPercentage())
      val (trainingData, testData) = (splits(0), splits(1))
      var paramsFeilds = ""
      var hypertunningFlag = RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfHyperParameterTunning().toString

      if (!RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfHyperParameterTunning()) {

        var rfClassifier = new RandomForestClassifier()
          .setLabelCol(commonLabelCol) // Double User
          .setFeaturesCol("features") // after vectorIndexer
          .setMaxBins(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMaxBins()(0))
          .setMaxDepth(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMaxDepth()(0))
          .setImpurity(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfImpurity()(0))
          .setMinInfoGain(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMinimumInfoGain()(0))
          .setMinInstancesPerNode(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMinimumInstancePerNode()(0))
          .setFeatureSubsetStrategy(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfFeatureSubsetStrategy()(0))
          .setNumTrees(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfNumTree()(0))
          .setSubsamplingRate(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfSubSamplingRate()(0)) //The default (1.0) is recommended, but decreasing this fraction can speed up training.
          .setCacheNodeIds(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfIsCacheNodeIds())
        //save model  
        model = rfClassifier.fit(trainingData)
        predictedDF = model.transform(testData)
        //model -- not perfect 
        
        predictedDF.show
        //multi binary 

        //      val metrics:MulticlassMetrics = new MulticlassMetrics(testData)

      } else if (RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfHyperParameterTunning()) {
        println("in else condition")
        var rfClassifier = new RandomForestClassifier()
          .setLabelCol(commonLabelCol)
          .setFeaturesCol("features")

        val paramGrid = new ParamGridBuilder()
          .addGrid(rfClassifier.maxBins, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMaxBins())
          .addGrid(rfClassifier.maxDepth, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMaxDepth())
          .addGrid(rfClassifier.impurity, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfImpurity())
          .addGrid(rfClassifier.minInfoGain, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMinimumInfoGain())
          .addGrid(rfClassifier.minInstancesPerNode, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfMinimumInstancePerNode())
          .addGrid(rfClassifier.featureSubsetStrategy, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfFeatureSubsetStrategy())
          .addGrid(rfClassifier.numTrees, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfNumTree())
          .addGrid(rfClassifier.subsamplingRate, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfSubSamplingRate())
          .build()
        //binary and multiClass check

        //need to change 
        var hyperPtObj = new HyperParaMeterTuner()
        var (pmap, outModel, pdf) = hyperPtObj.hyperParameterTuning(HyperParameterTunningProperties(userId, pipeLineId, branchId).getKfold(), trainingData, testData, paramGrid, evaluator, rfClassifier, "RandomForestClassification", HyperParameterTunningProperties(userId, pipeLineId, branchId).getSeed())
        println("-----------------------")
        pdf.show
        predictedDF = pdf
        model = outModel.asInstanceOf[RandomForestClassificationModel]
        var counterVar = 0
        pmap.toSeq.foreach(pair => {
          counterVar = counterVar + 1
          if (pmap.toSeq.size == counterVar) {
            paramsFeilds = paramsFeilds + s"${pair.param.name}" + "::" + pair.value
          } else {
            paramsFeilds = paramsFeilds + s"${pair.param.name}" + "::" + pair.value + " , "

          }
          println(s"${pair.param.name} ::")
          println(pair.value)
        })
      }

      accuracy = evaluator.evaluate(predictedDF)
      //for metrics

//      val predictionsData = predictedDF.select("prediction").rdd.map(_.getDouble(0))
//      val labelsData = predictedDF.select(commonLabelCol).rdd.map(_.getDouble(0))
      val columns = Seq[String]("prediction", commonLabelCol)
      val colNames = columns.map(name => col(name))
      println(" XXXXXXXXXXXXXXXXXXXXXXXXX  predicted df in randomForest  Algorithms    XXXXXXXXXXXXXXXXXXXX")
      predictedDF.show()
      
      var rdddf=predictedDF.rdd
      var outputStorageDir="hdfs://localhost:8020/AlsOutput/randomforest"
      val conf = new Configuration()
      conf.set("fs.defaultFS", "hdfs://localhost:8020")
      val fs= FileSystem.get(conf)
      checkExistsAndDelete(outputStorageDir,fs)
      
      rdddf.saveAsTextFile(outputStorageDir)
      
      
      
      
      
      val predictedoutputDF = predictedDF.select(colNames: _*)
      //predictedoutputDF.show(1000)

      var testrddOfDouble = predictedoutputDF.rdd.map(row => (row(0).toString().toDouble, row(1).toString().toDouble))

      var metricMap: LinkedHashMap[String, Any] = new LinkedHashMap[String, Any]()
      var rfClassificationMetric: LinkedHashMap[String, Any] = new LinkedHashMap[String, Any]()

      if (RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfIsMultiClass()) {
        var classificationMetricSeqOfMap: Seq[LinkedHashMap[String, Any]] = Nil
        var confusionMetricMap: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
        var confusionmetricSeqMap: Seq[LinkedHashMap[String, String]] = Nil

        var metrics = new MulticlassMetrics(testrddOfDouble)
        metricMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).RF_MODEL_STORAGE_PATH, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath())
        metricMap.put("Accuracy", (metrics.accuracy).toString())
        var arrayLabel = metrics.labels
        var Accuracy = metrics.accuracy
        println("Accuracy " + Accuracy)
        *//**
         * for label
         * this label String is Header of Confusion matrix in json
         *
         *//*
        var labelData: String = " ,"
        var seqOflabels: Seq[String] = Nil
        //var revLabels = arrayLabel.reverse
        for (i <- 0 until arrayLabel.length) {
          if (i == arrayLabel.length - 1) {
            labelData += arrayLabel { i }
          } else if (i < arrayLabel.length - 1) {
            labelData += arrayLabel { i } + ","
          } else if (i == 0) {
            labelData += arrayLabel { i }

          }

        }
        confusionMetricMap.put("rowid", "1")
        confusionMetricMap.put("rowValues", labelData)
        confusionmetricSeqMap = confusionmetricSeqMap :+ confusionMetricMap

        var confusionMatrix = metrics.confusionMatrix
        println("DTcfg")
        println("labels => " + arrayLabel.mkString(":"))
        var lbltp: LinkedHashMap[Double, Double] = new LinkedHashMap[Double, Double]()
        var lblfp: LinkedHashMap[Double, Double] = new LinkedHashMap[Double, Double]()
        var lblfn: LinkedHashMap[Double, Double] = new LinkedHashMap[Double, Double]()
        var lblprecision: LinkedHashMap[Double, Double] = new LinkedHashMap[Double, Double]()
        var lblrecall: LinkedHashMap[Double, Double] = new LinkedHashMap[Double, Double]()

        for (i <- 0 until confusionMatrix.numRows) {
          for (j <- 0 until confusionMatrix.numCols) {
            println(" (i,j)  " + i + " " + j + " val " + confusionMatrix(i, j).toString())

          }
        }
        
       * To calculate tpi , fpi , fni
       
        var totalLabels = metrics.labels
        var sizeOfLabels = totalLabels.size
        for (i <- 0 until sizeOfLabels) {
          var fp = 0.0
          for (j <- 0 until sizeOfLabels) {
            if (j != i) {
              fp = fp + confusionMatrix(i, j)
            }
          }
          var fn = 0.0
          for (j <- 0 until sizeOfLabels) {
            if (j != i) {
              fn = fn + confusionMatrix(j, i)
            }
          }
          var tp = 0.0
          tp = confusionMatrix(i, i)
          lbltp.put(metrics.labels { i }, tp)
          lblfp.put(metrics.labels { i }, fp)
          lblfn.put(metrics.labels { i }, fn)
          var precisionval = tp / (tp + fp)
          var recallval = tp / (tp + fn)
          lblprecision.put(metrics.labels { i }, precisionval)
          lblrecall.put(metrics.labels { i }, recallval)

        }
        // println("true positive rate "+metrics.truePositiveRate(0))
        // println("true negative rate "+metrics.f)
        for ((k, v) <- lbltp) {
          println("  lblfp k" + k + " lblfp v " + v)
        }
        for ((k, v) <- lblfp) {
          println("  lblfp k" + k + " lblfp v " + v)
        }
        for ((k, v) <- lblfn) {
          println("  lblfn k" + k + " lblfn v " + v)
        }

        var confusionMatrixData = metrics.confusionMatrix.toString.split("\n")
        for (i <- 0 until confusionMatrix.numRows) {
          var confusionMetricData: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
          confusionMetricData.put("rowid", (i.toInt + 2).toString())
          var valuesOfRow: String = ""
          for (j <- 0 until confusionMatrix.numCols) {
            if (j == confusionMatrix.numCols - 1) {
              valuesOfRow = valuesOfRow + confusionMatrix(i, j).toString()
            } else {
              valuesOfRow = valuesOfRow + confusionMatrix(i, j).toString() + ","
            }

          }
          confusionMetricData.put("rowValues", arrayLabel { i } + "," + valuesOfRow)
          confusionmetricSeqMap = confusionmetricSeqMap :+ confusionMetricData
        }
        //for loop for confusion metrics
        metricMap.put("ConfusionMetric", confusionmetricSeqMap)

        for (i <- 0 until confusionmetricSeqMap.length) {
          for ((k, v) <- confusionmetricSeqMap { i }) {
            println("k => " + k + "  v=> " + v)
          }
        }

        
       *  Evaluate micro average precision , recall and fmeasure
       *  precisionmicro  = tp1+tp2/(tp1+tp2+fp1+fp2)
       *  recallmicro = tp1+tp1/(tp1+tp2+fn1+fn2)
       *  fmeasuremicro=  2 / (1/precisionmicro + 1/recallmicro)
       

        var precisionMicroAvg = 0.0
        var recallMicroAvg = 0.0
        var fmeasureMicroAvg = 0.0
        var tpsum = 0.0
        var fpsum = 0.0
        var fnsum = 0.0
        for (i <- 0 until metrics.labels.size) {
          tpsum = tpsum + lbltp.get(metrics.labels { i }).get
          fpsum = fpsum + lblfp.get(metrics.labels { i }).get
          fnsum = fnsum + lblfn.get(metrics.labels { i }).get
        }
        precisionMicroAvg = tpsum / (tpsum + fpsum)
        recallMicroAvg = tpsum / (tpsum + fnsum)
        fmeasureMicroAvg = 2 / (1 / precisionMicroAvg + 1 / recallMicroAvg)

//        println("precisionMicroAvg , recallMicroAvg , fmeasureMicroAvg "
//          + precisionMicroAvg + "," + recallMicroAvg + "," + fmeasureMicroAvg)
        
       * Evaluate macro average precision , recall and fmeasure
       *  precisionmacro  = (p1+p2)/2
       *  recallmacro = (r1+r2) /2
       *  fmeasuremacro=  2 / (1/precisionmacro + 1/ recallmacro)
        

        var precisionMacroAvg = 0.0
        var recallMacroAvg = 0.0
        var fmeasureMacroAvg = 0.0
        var precisionsum = 0.0
        var recallsum = 0.0
        for (i <- 0 until metrics.labels.size) {
          precisionsum = precisionsum + lblprecision.get(metrics.labels { i }).get
          recallsum = recallsum + lblrecall.get(metrics.labels { i }).get
        }
        precisionMacroAvg = precisionsum / (sizeOfLabels)
        recallMacroAvg = recallsum / (sizeOfLabels)
        fmeasureMacroAvg = 2 / (1 / precisionMacroAvg + 1 / recallMacroAvg)

        println("debugEvaluator RandomForestClassification : precisionMacroAvg , recallMacroAvg , fmeasureMacroAvg ,precisionMicroAvg , recallMicroAvg , fmeasureMicroAvg , Accuracy "
          + precisionMacroAvg + "," + recallMacroAvg + "," + fmeasureMacroAvg + ", "+ precisionMicroAvg + "," + recallMicroAvg + "," + fmeasureMicroAvg +" , "+ Accuracy)
        println("precisionMacroAvg "+precisionMacroAvg)
        println("recallMacroAvg "+recallMacroAvg)
        println("fmeasureMacroAvg "+fmeasureMacroAvg)
        println("precisionMicroAvg "+precisionMicroAvg)
        println("recallMicroAvg "+recallMicroAvg)
        println("fmeasureMicroAvg "+fmeasureMicroAvg)
        println("Accuracy "+Accuracy)

        var labelCorrespondMetricSeqMap: Seq[LinkedHashMap[String, String]] = Nil
        for (i <- 0 until metrics.labels.length) {
          var confusionmetricMap: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
          confusionmetricMap.put("Label", (metrics.labels { i }).toString())
          var tp = lbltp.get(metrics.labels { i }).get
          var fp = lblfp.get(metrics.labels { i }).get
          var fn = lblfn.get(metrics.labels { i }).get
          var tn = (rowsIndf - (tp+fp+fn))
          confusionmetricMap.put("truePositive", tp.toString())
          confusionmetricMap.put("trueNegative", tn.toString())
          confusionmetricMap.put("falsePositive", fp.toString())
          confusionmetricMap.put("falseNegative", fn.toString())
          confusionmetricMap.put("FMeasure", (metrics.fMeasure(metrics.labels { i })).toString())
          confusionmetricMap.put("Precision", (metrics.precision(metrics.labels { i })).toString())
          confusionmetricMap.put("Recall", (metrics.recall(metrics.labels { i })).toString())
          labelCorrespondMetricSeqMap = labelCorrespondMetricSeqMap :+ confusionmetricMap
          println(" confuse matrix is    ")
          confusionmetricMap.foreach {keyVal => println(keyVal._1 + "=" + keyVal._2)}

        }
        
        
        
        
        for (i <- 0 until labelCorrespondMetricSeqMap.length) {
          for ((k, v) <- labelCorrespondMetricSeqMap { i }) {
            println("lk => " + k + "  lv=> " + v)
          }
        }
        metricMap.put("LabelCorrespondMetric", confusionmetricSeqMap)
        //      metricMap.put("modelOutput",DecisionTreeProperties(userId,pipeLineId,branchId).getModelStorageDir())
        classificationMetricSeqOfMap = classificationMetricSeqOfMap :+ metricMap
        rfClassificationMetric.put("RandomForestClassificationMetrics", classificationMetricSeqOfMap)
        
      * Fill in null model when eval
      
        
     *  To Handle evaluation metrices
     *  
     
        var evaluationMetricName = RandomForestRegressionProperties(userId, pipeLineId, branchId).getEvaluatorMetric()
        var evaluatorPercentage = RandomForestRegressionProperties(userId, pipeLineId, branchId).getEvaluatorPercentage()
        var firstTimeEvaluationval = RandomForestRegressionProperties(userId, pipeLineId, branchId).getFirstTimeEvaluationVal()
        if (evaluationMetricName != null && !evaluationMetricName.isEmpty()) {
          var isEvaluationPassed = false
          var metricVal = ElasticSearchSinkHelper.searchClassificationQuery(CommonProperties.getElasticIndex(), Constants.MULTINOMIAL_CLASSIFICATION_INDEXTYPE, userId, pipeLineId,
            branchId, Constants.RANDOMFORESTCLASSIFICATION, "classification", "multinomial", dfCount.toString(), evaluationMetricName)

          if (evaluationMetricName.equalsIgnoreCase("fmeasureMicro")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (fmeasureMicroAvg >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((fmeasureMicroAvg - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)

              }
            }
          } else if (evaluationMetricName.equalsIgnoreCase("fmeasureMacro")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (fmeasureMacroAvg >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((fmeasureMacroAvg - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("precisionMicro")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (precisionMicroAvg >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((precisionMicroAvg - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("precisionMacro")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (precisionMacroAvg >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((precisionMacroAvg - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("recallMicro")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (recallMicroAvg >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((recallMicroAvg - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("recallMacro")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (recallMacroAvg >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((recallMacroAvg - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("Accuracy")) {
            if (metricVal == null || metricVal.isEmpty()) {
              println("inside accurary e m accurary val  and firstTimeEvaluationval  " + Accuracy + "," + firstTimeEvaluationval)

              if (Accuracy >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            } else {

              var metricValInDouble = metricVal.toDouble
              println("inside accurary e m accurary val  and percentage  " + Accuracy + "," + metricValInDouble)
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((Accuracy - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
                  CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
                  fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)
              }
            }

          }
         
         * Checking that evaluation is set but evaluation case is not passed
         
        if(isEvaluationPassed == false)
        {
          CommonProperties.setCanContinueProgram(false)
        }
        } else {
          
         * Non evalaution metrices scenario
         
          randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
          RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
          ElasticSearchSinkHelper.saveJsonForMultinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification",
            CommonProperties.getBranchPath(), hypertunningFlag, paramsFeilds, "multinomial", (metrics.accuracy).toString(),
            fmeasureMicroAvg.toString(), fmeasureMacroAvg.toString(), precisionMicroAvg.toString(), precisionMacroAvg.toString(), recallMicroAvg.toString(), recallMacroAvg.toString(), labelCorrespondMetricSeqMap, confusionmetricSeqMap)

        }

        //      var classificationMetricSeqOfMap: Seq[LinkedHashMap[String, Any]] = Nil
        //      var confusionMetricMap: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
        //      var confusionmetricSeqMap: Seq[LinkedHashMap[String, String]] = Nil
        //
        //      var metrics = new MulticlassMetrics(predictionsData.zip(labelsData))
        //      metricMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).RF_MODEL_STORAGE_PATH, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath())
        //      metricMap.put("Accuracy", (metrics.accuracy).toString())
        //
        //      println("Confusion Matrix : " + metrics.confusionMatrix)
        //      var arrayLabel = metrics.labels
        //      /**
        //       * for label
        //       * this label String is Header of Confusion matrix in json
        //       *
        //       */
        //      var labelData: String = " ,"
        //      var seqOflabels: Seq[String] = Nil
        //      var revLabels = arrayLabel.reverse
        //      for (i <- 0 until revLabels.length) {
        //        if (i == revLabels.length - 1) {
        //          labelData += revLabels { i }
        //        } else if (i < revLabels.length - 1) {
        //          labelData += revLabels { i } + ","
        //        } else if (i == 0) {
        //          labelData += revLabels { i }
        //
        //        }
        //
        //      }
        //      confusionMetricMap.put("rowid", "1")
        //      confusionMetricMap.put("rowValues", labelData)
        //      confusionmetricSeqMap = confusionmetricSeqMap :+ confusionMetricMap
        //
        //      var confusionMatrix = metrics.confusionMatrix
        //      var newMatrix = Array.ofDim[String](confusionMatrix.numRows, confusionMatrix.numCols)
        //
        //      /**
        //       * to swap rows and columns
        //       */
        //      for (i <- 0 until confusionMatrix.numRows) {
        //        var seqOfColumns: Seq[String] = Nil
        //        var revseqOfColumns: Seq[String] = Nil
        //        for (j <- 0 until confusionMatrix.numCols) {
        //          //println("if=>"+i+" jf=>"+j)
        //          seqOfColumns = seqOfColumns :+ confusionMatrix(i, j).toString()
        //          revseqOfColumns = seqOfColumns.reverse
        //
        //        }
        //        println(revseqOfColumns.mkString(","))
        //        for (j <- 0 until confusionMatrix.numCols) {
        //          //println("is=>"+i+" js=>"+j)
        //          newMatrix(i)(j) = revseqOfColumns { j }.toString()
        //        }
        //      }
        //
        //      for (i <- 0 until confusionMatrix.numCols) {
        //        var seqOfRows: Seq[String] = Nil
        //        var revseqOfRows: Seq[String] = Nil
        //        for (j <- 0 until confusionMatrix.numRows) {
        //
        //          seqOfRows = seqOfRows :+ newMatrix(j)(i).toString()
        //          revseqOfRows = seqOfRows.reverse
        //          newMatrix(j)(i) = revseqOfRows { j }.toString()
        //
        //        }
        //        for (j <- 0 until confusionMatrix.numRows) {
        //
        //          newMatrix(j)(i) = revseqOfRows { j }.toString()
        //
        //        }
        //      }
        //
        //      var confusionMatrixData = metrics.confusionMatrix.toString.split("\n")
        //      for (i <- 0 until confusionMatrix.numRows) {
        //        var confusionMetricData: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
        //        confusionMetricData.put("rowid", (i.toInt + 2).toString())
        //        var valuesOfRow: String = ""
        //        for (j <- 0 until confusionMatrix.numCols) {
        //          if (j == confusionMatrix.numCols - 1) {
        //            valuesOfRow = valuesOfRow + confusionMatrix(i, j).toString()
        //          } else {
        //            valuesOfRow = valuesOfRow + confusionMatrix(i, j).toString() + ","
        //          }
        //
        //        }
        //        confusionMetricData.put("rowValues", revLabels { i } + "," + valuesOfRow)
        //        confusionmetricSeqMap = confusionmetricSeqMap :+ confusionMetricData
        //      }
        //      //for loop for confusion metrics
        //      metricMap.put("ConfusionMetric", confusionmetricSeqMap)
        //
        //      confusionmetricSeqMap = Nil
        //      for (i <- 0 until metrics.labels.length) {
        //        var confusionmetricMap: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
        //        confusionmetricMap.put("Label", (metrics.labels { i }).toString())
        //        confusionmetricMap.put("FMeasure", (metrics.fMeasure(metrics.labels { i })).toString())
        //        confusionmetricMap.put("Precision", (metrics.precision(metrics.labels { i })).toString())
        //        confusionmetricMap.put("Recall", (metrics.recall(metrics.labels { i })).toString())
        //        confusionmetricSeqMap = confusionmetricSeqMap :+ confusionmetricMap
        //
        //      }
        //      metricMap.put("LabelCorrespondMetric", confusionmetricSeqMap)
        //      //      metricMap.put("modelOutput",RandomForestRegressionProperties(userId,pipeLineId,branchId).getRfModelStoragePath())
        //      classificationMetricSeqOfMap = classificationMetricSeqOfMap :+ metricMap
        //      rfClassificationMetric.put("RandomForestClassificationMetrics", classificationMetricSeqOfMap)

      } else {
        //BinaryClass
        *//**
         *
         * Getting confusion matrix
         *
         *//*
        var classificationSeqOfMap: Seq[LinkedHashMap[String, Any]] = Nil
        metricMap = new LinkedHashMap[String, Any]()
        var confusionMetricSeqOfMap: Seq[LinkedHashMap[String, String]] = Nil
        var confusionMetricMap: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
        println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX Prediction df is  in Binary Class XXXXXXXXXXXXXXX")
        predictedDF.show()
        
        var confusionMatrixBinary = predictedDF.stat.crosstab("prediction", commonLabelCol)
        println(" \" \" \" \"  confusionMatrixBinary   \\\\\\\\\\\\\\ ")
        confusionMatrixBinary.show()

        //      var metrics = new MulticlassMetrics(predictionsData.zip(labelsData))
        //      var accuracyNew = metrics.accuracy
        metricMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).RF_MODEL_STORAGE_PATH, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath())
        *//**
         * changes for confusion matrix
         *//*
        var confusionMatrixDataNew = confusionMatrixBinary.collect.map(_.toSeq.map(_.toString))
        var confusionMatrixHeader = confusionMatrixBinary.columns
        var truePositive: Double = 0.0
        var falsePositive: Double = 0.0
        var falseNegative: Double = 0.0
        var trueNegative: Double = 0.0
        *//**
         *
         * New changes calculating TP FP TN FN by fixing label header and looping through rows
         * to find values
         *
         *//*
        var firstColumn: Double = -1.0
        var secondColumn: Double = -1.0
        var (restruePositive, resfalsePositive, restrueNegative, resfalseNegative) = CommonUtil.calculateConfusionMatrixResults(firstColumn, secondColumn, truePositive, falsePositive, trueNegative, falseNegative, confusionMatrixHeader, confusionMatrixDataNew)
        truePositive = restruePositive
        falsePositive = resfalsePositive
        trueNegative = restrueNegative
        falseNegative = resfalseNegative
        // mapping it to zero and 1 for saving in elastic search 
        // as though one of them can be -1 but we need to  tell 0 is  
        firstColumn = 0.0
        secondColumn = 1.0
        precisionVal = truePositive / (truePositive + falsePositive)
        recallVal = truePositive / (truePositive + falseNegative)
        println(" -confusion matrix is - ", truePositive, falsePositive, falseNegative, trueNegative)
        var accurayCheck = (truePositive + trueNegative) / (truePositive + trueNegative + falsePositive + falseNegative)
        //      println("------------- accurcy ",accuracyNew)
        metricMap.put("Accuracy", accurayCheck.toString())
        accuracyVal = accurayCheck
        //Changes for area under pr and area under roc
        var metricsToFindArea = new BinaryClassificationMetrics(testrddOfDouble)
        var precision = metricsToFindArea.precisionByThreshold()
        areaUnderRocval = metricsToFindArea.areaUnderROC()
        areaunderPRval = metricsToFindArea.areaUnderPR()
        println("debugEvaluator RandomForestClassification :")
        println("precisionVal " + precisionVal)
        println("recallVal " + recallVal)
        println("accuracyVal " + accuracyVal)
        println("areaunderPRval " + areaunderPRval)
        println("areaUnderRocval " + areaUnderRocval)
        metricMap.put("Area Under Precision-Recall Curve", metricsToFindArea.areaUnderPR().toString())
        metricMap.put("Area Under ROC", metricsToFindArea.areaUnderROC().toString())
        *//**
         *
         *
         * checking
         *
         *//*

        //      var metrics = new MulticlassMetrics(predictionsData.zip(labelsData))
        //      var accurayCheckNew  = metrics.accuracy
        //      println(" ---------------------------- accuracy check frm multi",accurayCheckNew)
        println("-------------------------------- from confusion matrix ", accurayCheck)
        *//**
         *
         * code for points
         * https://stackoverflow.com/questions/38207990/how-to-plot-roc-curve-and-precision-recall-curve-from-binaryclassificationmetric
         *//*

        //      var testing = De
        var points = metricsToFindArea.roc().collect()
        var pRCurvePoints = metricsToFindArea.pr().collect()
        println("--------- points ---------- ------ roc", points)
        points.foreach(println)
        println("--------- points ---------- ------ pr curve")
        pRCurvePoints.foreach(println)

        *//**
         * for label
         * this label String is Header of Confusion matrix in json
         *
         *//*
        var labelData: String = ", Prediction Positive , Prediction Negative"
        var revLabels: Array[String] = Array("Condition Positive", "Condition Negative")
        confusionMetricMap.put("rowid", "1")
        confusionMetricMap.put("rowValues", labelData)
        confusionMetricSeqOfMap = confusionMetricSeqOfMap :+ confusionMetricMap

        for (i <- 0 until 2) {
          var confusionMetricData: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
          confusionMetricData.put("rowid", (i.toInt + 2).toString())
          var valuesOfRow: String = ""
          if (i == 0) {
            valuesOfRow = truePositive.toString() + "," + falsePositive.toString()
          } else {
            valuesOfRow = falseNegative.toString() + "," + trueNegative.toString()
          }

          println(" row ", (i.toInt + 2).toString(), valuesOfRow)
          confusionMetricData.put("rowValues", revLabels { i } + "," + valuesOfRow)
          confusionMetricSeqOfMap = confusionMetricSeqOfMap :+ confusionMetricData
         // println(" XXXXXXXXXXXXXXXXXXXXXXXXXXXXX confuse matrix is  in Binary Class XXXXXXXXXXXXXXX  ")
         // confusionMetricData.foreach {keyVal => println(keyVal._1 + "=" + keyVal._2)}
        }

        metricMap.put("ConfusionMetric", confusionMetricSeqOfMap)
        classificationSeqOfMap = classificationSeqOfMap :+ metricMap
        rfClassificationMetric.put("RandomForestClassificationMetrics", classificationSeqOfMap)

        
     *  To Handle evaluation metrices
     *  
     
        var evaluationMetricName = RandomForestRegressionProperties(userId, pipeLineId, branchId).getEvaluatorMetric()
        var evaluatorPercentage = RandomForestRegressionProperties(userId, pipeLineId, branchId).getEvaluatorPercentage()
        var firstTimeEvaluationval = RandomForestRegressionProperties(userId, pipeLineId, branchId).getFirstTimeEvaluationVal()
        if (evaluationMetricName != null && !evaluationMetricName.isEmpty()) {
          var isEvaluationPassed = false
          var metricVal = ElasticSearchSinkHelper.searchClassificationQuery(CommonProperties.getElasticIndex(), Constants.BINOMIAL_CLASSIFICATION_INDEXTYPE, userId, pipeLineId,
            branchId, Constants.RANDOMFORESTCLASSIFICATION, "classification", "binomial", dfCount.toString(), evaluationMetricName)

          if (evaluationMetricName.equalsIgnoreCase("areaUnderROCData")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (areaUnderRocval >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((areaUnderRocval - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            }
          } else if (evaluationMetricName.equalsIgnoreCase("Accuracy")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (accuracyVal >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((accuracyVal - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("Precision")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (precisionVal >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((precisionVal - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("Recall")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (recallVal >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((recallVal - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            }

          } else if (evaluationMetricName.equalsIgnoreCase("AreaUnderPR")) {
            if (metricVal == null || metricVal.isEmpty()) {
              if (areaunderPRval >= firstTimeEvaluationval.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            } else {
              var metricValInDouble = metricVal.toDouble
              println("metricValInDouble " + metricValInDouble)
              var percentage = ((areaunderPRval - metricValInDouble) / metricValInDouble) * 100
              println("percentage " + percentage + " evaluatorPercentage " + evaluatorPercentage)
              if (percentage >= evaluatorPercentage.toDouble) {
                isEvaluationPassed = true
                randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
                RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
                ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
                  "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
              }
            }

          }
         
         * Checking that evaluation is set but evaluation case is not passed
         
        if(isEvaluationPassed == false)
        {
          CommonProperties.setCanContinueProgram(false)
        }
        } else {
          
         * Non evaluation metric scenario
         
          randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
          RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
          ElasticSearchSinkHelper.saveJsonForBinomialClassificationAlgo(userId, pipeLineId, branchId, Constants.RANDOMFORESTCLASSIFICATION, dfCount.toString(), "classification", CommonProperties.getBranchPath(), hypertunningFlag,
            "binomial", paramsFeilds, truePositive.toString(), trueNegative.toString(), falsePositive.toString(), falseNegative.toString(), firstColumn.toString(), secondColumn.toString(), areaUnderRocval.toString(), accuracyVal.toString(), areaunderPRval.toString(), precisionVal.toString(), recallVal.toString())
        }

        //      var classificationSeqOfMap: Seq[LinkedHashMap[String, Any]] = Nil
        //      metricMap = new LinkedHashMap[String, Any]()
        //      var confusionMetricSeqOfMap: Seq[LinkedHashMap[String, String]] = Nil
        //      var confusionMetricMap: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
        //
        //      var confusionMatrixBinary = predictedDF.stat.crosstab("prediction", commonLabelCol)
        //      confusionMatrixBinary.show
        //
        //      var metrics = new MulticlassMetrics(predictionsData.zip(labelsData))
        //      metricMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).RF_MODEL_STORAGE_PATH, RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath())
        //      metricMap.put("Accuracy", metrics.accuracy.toString())
        //
        //      //Changes for area under pr and area under roc
        //      var metricsToFindArea = new BinaryClassificationMetrics(predictionsData.zip(labelsData))
        //      metricMap.put("Area Under Precision-Recall Curve", metricsToFindArea.areaUnderPR().toString())
        //      metricMap.put("Area Under ROC", metricsToFindArea.areaUnderROC().toString())
        //
        //      /**
        //       * for label
        //       * this label String is Header of Confusion matrix in json
        //       *
        //       */
        //      var labelData: String = ", Prediction Positive , Prediction Negative"
        //      var revLabels: Array[String] = Array("Condition Positive", "Condition Negative")
        //      confusionMetricMap.put("rowid", "1")
        //      confusionMetricMap.put("rowValues", labelData)
        //      confusionMetricSeqOfMap = confusionMetricSeqOfMap :+ confusionMetricMap
        //
        //      /**
        //       *
        //       * For confusion matric
        //       *
        //       */
        //      var confusionMatrix = metrics.confusionMatrix
        //      var newMatrix = Array.ofDim[String](confusionMatrix.numRows, confusionMatrix.numCols)
        //      /**
        //       * to swap rows and columns
        //       */
        //      for (i <- 0 until confusionMatrix.numRows) {
        //        var seqOfColumns: Seq[String] = Nil
        //        var revseqOfColumns: Seq[String] = Nil
        //        for (j <- 0 until confusionMatrix.numCols) {
        //          //println("if=>"+i+" jf=>"+j)
        //          seqOfColumns = seqOfColumns :+ confusionMatrix(i, j).toString()
        //          revseqOfColumns = seqOfColumns.reverse
        //
        //        }
        //        println(revseqOfColumns.mkString(","))
        //        for (j <- 0 until confusionMatrix.numCols) {
        //          //println("is=>"+i+" js=>"+j)
        //          newMatrix(i)(j) = revseqOfColumns { j }.toString()
        //        }
        //      }
        //
        //      for (i <- 0 until confusionMatrix.numCols) {
        //        var seqOfRows: Seq[String] = Nil
        //        var revseqOfRows: Seq[String] = Nil
        //        for (j <- 0 until confusionMatrix.numRows) {
        //
        //          seqOfRows = seqOfRows :+ newMatrix(j)(i).toString()
        //          revseqOfRows = seqOfRows.reverse
        //          newMatrix(j)(i) = revseqOfRows { j }.toString()
        //
        //        }
        //        for (j <- 0 until confusionMatrix.numRows) {
        //
        //          newMatrix(j)(i) = revseqOfRows { j }.toString()
        //
        //        }
        //      }
        //
        //      var confusionMatrixData = metrics.confusionMatrix.toString.split("\n")
        //      for (i <- 0 until confusionMatrix.numRows) {
        //        var confusionMetricData: LinkedHashMap[String, String] = new LinkedHashMap[String, String]()
        //        confusionMetricData.put("rowid", (i.toInt + 2).toString())
        //        var valuesOfRow: String = ""
        //        for (j <- 0 until confusionMatrix.numCols) {
        //          if (j == confusionMatrix.numCols - 1) {
        //            valuesOfRow = valuesOfRow + newMatrix(i)(j).toString()
        //          } else {
        //            valuesOfRow = valuesOfRow + newMatrix(i)(j).toString() + ","
        //          }
        //
        //        }
        //        confusionMetricData.put("rowValues", revLabels { i } + "," + valuesOfRow)
        //        confusionMetricSeqOfMap = confusionMetricSeqOfMap :+ confusionMetricData
        //      }
        //
        //      metricMap.put("ConfusionMetric", confusionMetricSeqOfMap)
        //      classificationSeqOfMap = classificationSeqOfMap :+ metricMap
        //      rfClassificationMetric.put("RandomForestClassificationMetrics", classificationSeqOfMap)

     
      
      
      
      
      } // end of binary clasification
      
      
      
      
      
      
      
      
      
      //    randomForestClassificationMetricHashMap.put(RandomForestRegressionProperties(userId,pipeLineId,branchId).getRfMetricStorageOutputFile(), rfClassificationMetric)
      //    randomForestClassificationModelHashMap.put(RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath(), model)
      //    RandomForestAttributes(userId, pipeLineId, branchId).setRfClassificationModel(randomForestClassificationModelHashMap)
      //   RandomForestAttributes(userId,pipeLineId,branchId).setRfMetric(randomForestClassificationMetricHashMap)
      //    LogisticRegressionProperties
      //    /*
      //    * To handle intermidiate state
      //    */
      //    var hdfsUri = RandomForestRegressionProperties(userId, pipeLineId, branchId).getHdfsUri()
      //    val conf: Configuration = new Configuration()
      //    var sparkSession = CommonProperties.getSparkSession()
      //
      //    //var dataHeaderPresent = CommonProperties.getDataHeaderPresent()
      //    conf.set("fs.default.name", hdfsUri)
      //    conf.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName)
      //    conf.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)
      //
      //    var uri: URI = URI.create(hdfsUri)
      //    var fileSystem = FileSystem.get(uri, conf)
      //    var viewintermidiateState = RandomForestRegressionProperties(userId, pipeLineId, branchId).getViewIntermediate()
      //    if (viewintermidiateState.equalsIgnoreCase("true")) {
      //
      //      var sparkSession = CommonProperties.getSparkSession()
      //      var dataHeaderPresent = CommonProperties.getDataHeaderPresent()
      //      var algoname = Constants.RANDOMFORESTCLASSIFICATION
      //      var outputStorageDir = "/tmp" + "/" + userId + "/" + pipeLineId + "/" + branchId + "/" + algoname + "/" + dfCount
      //      if (fileSystem.exists(new Path(outputStorageDir))) {
      //        fileSystem.delete(new Path(outputStorageDir), true);
      //      }
      //      CommonUtil.writeToHDFS(sparkSession, df, dataHeaderPresent, outputStorageDir)
      //    }
    } catch {
      case e: Exception =>
        {
          CommonUtil.exceptionCatch(e, outpath)
        }

    }

    (df, model)

  }

  *//**
   *
   * multi Df
   *//*
  def randomForestClassificationProcessingMultipleDf(dataframes: Seq[DataFrame]): Seq[DataFrame] = {
    var outputDfs: Seq[DataFrame] = Nil
    var rfModelStoragePath = RandomForestRegressionProperties(userId, pipeLineId, branchId).getRfModelStoragePath()
    //   var rfMetricStorageOutputFile = RandomForestRegressionProperties(userId,pipeLineId,branchId).getRfMetricStorageOutputFile()

    for (i <- 0 to (dataframes.length - 1)) {
      dfCount = i
      RandomForestRegressionProperties(userId, pipeLineId, branchId).setRfModelStoragePath(rfModelStoragePath + "/" + i)
      //     RandomForestRegressionProperties(userId,pipeLineId,branchId).setRfMetricStorageOutputFile(rfMetricStorageOutputFile + "/" + i)
      var (outdf, model) = randomForestClassificationModelPackage(dataframes { i })
      outputDfs = outputDfs :+ outdf
    }
    outputDfs
  }
 def checkExistsAndDelete(fileFullPath: String, fileSystem: FileSystem): Unit=
{
println("check if file Exists on hdfs And if it does then delete output dir on hdfs")
val path: Path = new Path(fileFullPath)
if (fileSystem.exists(path)) {
fileSystem.delete(path,true);
}
else
{
println("output directory does not exists")
}
return
}
}*/