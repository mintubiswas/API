package com.scala.spark

import org.apache.spark.sql.functions.{concat_ws, col}
//import org.apache.spark.sql.Row
import org.apache.spark.sql.Row
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
import org.apache.hadoop.fs.FSDataOutputStream

class AlsMlProduct() extends Serializable{

    val coder: ((Double) => Double) = (arg: Double) => {
    var a = arg
    var result=math.max(math.min(a, 1.0), 0.0)
    result
  }
    // def myFunc: (String => String) = { s => s.toLowerCase }
  //  val coders: (String => String,String => String) = (arg1: String, arg2: String) => {
    val coders: ((String,String)=>String) = (arg1: String , arg2: String) => {
      var count=""
      var output=""
    for (i <- 0 to (arg2.length - 1)) {
      var str = arg2.split("]")
      var counts = str.length
      //println(count)
      count= counts.toString()
      output=arg1 + "," + counts
    }
    output
  }
  val ratingfunc = udf(coder)

  val countfunc = udf(coders)
  var mysqlDummy: String = _
  var localsparkSession: SparkSession = _
  var productAffinityDelimiter: String = _
  var productWithAffinityDelimiter: String = _
  //  var alsRank: Int = _
  // var rankRange: Int = _
  var alsRankSeq: Seq[Int] = Nil
  var alsMetricName: String = _
  //var alsMaxIter: String = _
  var alsRegParamSeq: Seq[Double] = Nil
  var alsMaxIterationSeq: Seq[Int] = Nil
  var alsNumFeatures: Int = _
  var noOfRecommendations: Int = _
  // var alsNumIterations : Int =_
  var alsRecommendationsOutputDir: String = _
  var alsInputFile: String = _
  var alsNumOfRecommendations: String = _
  var alsLambda: Double = _
  var alsFeaturesDelimiter: String = _
  var alsTrainPercentage: Double = 0.7
  var alsTestPercentage: Double = 0.3
  var hdfsUri: String = _
  var alsMetricStoragePath: String = _
  var alsOutputInElasticsearch: String = _
  var alsOutputElasticsearchIndex: String = _
  var alsOutputElasticsearchUser: String = _
  var alsOutputElasticsearchPassword: String = _
  var alsModelStorageOutputDir: String = _
  var alsCoefficientStorageOutputDir: String = _
  var runningAlgoName = "ALS"
  var userCol: String = _
  var itemCol: String = _
  var ratingCol: String = _
  //var regParam : Double =_
  var alsMaxLoop: Int = _
  var hiveDatabase: String = ""
  var hiveTable: String = ""
  var hiveWarehouseDir: String = ""
  var hiveListOfCols: ArrayBuffer[String] = new ArrayBuffer()

  var hyperParameterTunning: String = _
  var dataHeaderPresent: String = _
  var kfold: Int = 3
  var seed: Long = 12345L
  var algoName: String = _
  var dataSource: String = _
  var alsminRating =1.0
  var alsmaxRating =10.0
  var hadoopConf: Configuration = _
  var fileSystem: FileSystem = _
  var outputpathalsMetric: LinkedHashMap[String, Map[String, Double]] = new LinkedHashMap[String, Map[String, Double]]()
  var outputpathUserRecommendations: LinkedHashMap[String, DataFrame] = new LinkedHashMap[String, DataFrame]()
  var outputpathModelLinkedHashMap: LinkedHashMap[String, MatrixFactorizationModel] = new LinkedHashMap[String, MatrixFactorizationModel]()
  var userRecommendations: DataFrame = null
  import java.io.BufferedWriter
  import java.io.OutputStreamWriter
  
  case class RatingNEW(user: Int, product: Int, rating: Double) extends Serializable
  object RatingNEW {
    def parseRating(str: String): RatingNEW = {
      val fields = str.split(",")
      assert(fields.size == 3)
      RatingNEW(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    }
  }
 

  def alsModelingPackage(): Unit = {
    println("inside mp")
    var sparkSession = SparkSession.builder.master("local").appName("Als Recommender").getOrCreate()
    println("in read data")
    var sc=sparkSession.sparkContext
    val df = sparkSession.read.option("header","true").csv("hdfs://localhost:8020/Recomender/data3.csv")
    df.show()
    var base_df = df
    var model: MatrixFactorizationModel = null
    var coefficientsList: Seq[Double] = Nil
   // var dataSource = "hdfs://localhost:8020/Recomender/data.csv"
    println("als dataSource is " + dataSource)
    alsMetricName = "SplitCollabClusters_ALS_Metric"
    
    var alsTrainPercentageVal = 0.7
   
    if (alsTrainPercentageVal != null) {
      alsTrainPercentage = alsTrainPercentageVal
    }

    var noOfRecommendationsInt = 1000
    if (noOfRecommendationsInt.isValidInt) {
      noOfRecommendations = noOfRecommendationsInt
    }
    var lambdaDouble = 0.1

    var alsTestPercentageVal = 0.3
    if (alsTestPercentageVal != null) {
      alsTestPercentage = alsTestPercentageVal
    }
    var alsMaxIterString ="5"
    if (alsMaxIterString != null && !(alsMaxIterString.isEmpty())) {
      alsMaxIterationSeq = alsMaxIterString.split(",").map(x => x.trim()).map(x => x.toInt)
    }

    var alsRankString ="30"
    if (alsRankString != null && !(alsRankString.isEmpty())) {
      alsRankSeq = alsRankString.split(",").map(x => x.toInt)
    }
    
    var alsminRatingDummy ="1"
    if (alsminRatingDummy != null && !(alsminRatingDummy.isEmpty())) {
      alsminRating = alsminRatingDummy.toDouble
    }
    
    var alsmaxRatingDummy = "5"
    if (alsmaxRatingDummy != null && !(alsmaxRatingDummy.isEmpty())) {
      alsmaxRating = alsmaxRatingDummy.toDouble
    }
    var userColString = "UserId"
    if (userColString != null && !(userColString.isEmpty())) {
      userCol = userColString

    }

    var itemColString = "ItemId"
    if (itemColString != null && !(itemColString.isEmpty())) {
      itemCol = itemColString

    }

    var ratingColString = "Affinity"
    if (ratingColString != null && !(ratingColString.isEmpty())) {
      ratingCol = ratingColString

    }
    println("userCol=>"+userCol + "itemCol=>"+itemCol +"ratingCol=>"+ratingCol)
    var dataHeaderPresent = true
    base_df = base_df.select(col(userCol), col(itemCol), col(ratingCol))
    val splits = base_df.randomSplit(Array(alsTrainPercentage, alsTestPercentage))

      val trainingData=base_df
      trainingData.show()
      
      println("als training data")
     // var sc = SparkSession.builder.master("local").appName("Spark SQL basic example").getOrCreate()
      //var scc=sc.sparkContext
     // val sqlContext = new SQLContext(sc)
      
     // import sqlContext.implicits._
      
      println("alsMEtricName" + alsMetricName + "alsMaxIter " + alsMaxIterationSeq.mkString(",") + " alsRegParam " + alsRegParamSeq.mkString(",") + " alsSolver " + alsRankSeq.mkString(","))
 
      var alsMaxIterArray = alsMaxIterationSeq.toArray
      var alsRegParamArray = alsRegParamSeq.toArray
      var alsRankArray = alsRankSeq.toArray

      println("alsMaxIterArray " + alsMaxIterArray.mkString(",") + " alsRegParamArray " + alsRegParamArray.mkString(",") + " alsRankArray " + alsRankArray.mkString(","))

      var data = trainingData.rdd.map(x => x.toString())
      /**
       *  user and item cols are typecasted to double and then to int to take double values as well
       */
      println("min rating is "+alsminRating +" max rating is "+alsmaxRating)
      var sum =((alsmaxRating - alsminRating) + 1)
      println("sum is " +sum)
      var meanValOfRating= sum / 2
      println(" mean rating "+meanValOfRating)
      var ratings = data.map(_.replaceAll("[\\[\\]]", "")).map { line =>
        val fields = line.split(",")
        Rating(fields(0).toString().toDouble.toInt, fields(1).toString().toDouble.toInt, fields(2).toDouble)
      }.cache()
      val binarizedRatings = ratings.map(r => Rating(r.user, r.product,
        if (r.rating > 0) 1.0 else 0.0)).cache()

      // Summarize ratings
      val numRatings = ratings.count()
      val numUsers = ratings.map(_.user).distinct().count()
      val numMovies = ratings.map(_.product).distinct().count()
      println(s"Got $numRatings ratings from $numUsers users on $numMovies movies.")
      //ratings.toDF().show()
      println("ratings df !!")
      model = ALS.train(ratings, alsRankArray(0), alsMaxIterArray(0), lambdaDouble)
      //scaling rating to minrating to maxrating
      def scaledRating(r: Rating): Rating = {
        val scaledRating = math.max(math.min(r.rating, alsmaxRating), alsminRating)
        Rating(r.user, r.product, scaledRating)
      }

      println("noOfRecommendations  " + noOfRecommendations)
      var userRecommended = model.recommendUsersForProducts(noOfRecommendations)
      userRecommended=userRecommended.map { case (user, recs) =>
      (user, recs.map(scaledRating))
    }
     
      var userRecommendedDf = userRecommended.toDF()
      println("user recommendation df ")

      var cols = userRecommendedDf.columns
      // case class recommendationstructure(user:Int,product:Int,rating:Double)
      val extractArray = udf[String, Seq[Any]](_.mkString(","))
      var secondCol = cols(1)
      userRecommendedDf = userRecommendedDf.withColumn(secondCol, extractArray(userRecommendedDf.col(secondCol)))
      userRecommendedDf.show()
      var x =userRecommendedDf.select("_2").collect()(0).get(0).toString()
      println(x)
      //     userRecommendedDf=userRecommendedDf.withColumn("_2", toSR(userRecommendedDf.col("_2")))
      //     userRecommendedDf.show()
       /*val conf = new Configuration()
      //conf.set("fs.defaultFS", "hdfs://quickstart.cloudera:8020")
      conf.set("fs.defaultFS", "hdfs://localhost:8020")
      val fs= FileSystem.get(conf)
      val output = fs.create(new Path("/"))
      
      var path1= "hdfs://localhost:8020/Recomender/output1.csv"
      var path2="hdfs://localhost:8020/Recomender/output2.csv"
      var path3="hdfs://localhost:8020/Recomender/output3.csv"
      outputpathUserRecommendations.put(path1, userRecommendedDf)
      ALSAttributes().setOutputpathUserRecommendations(outputpathUserRecommendations)*/
      // Assume that any movie a user rated 3 or higher (which maps to a 1) is a relevant document
      // Compare with top ten most relevant documents
      val userMovies = binarizedRatings.groupBy(_.user)
      val relevantDocuments = userMovies.join(userRecommended).map {
        case (user, (actual,
          predictions)) =>
          (predictions.map(_.product), actual.filter(_.rating > 0.0).map(_.product).toArray)
      }

      // Instantiate metrics object
      val metrics = new RankingMetrics(relevantDocuments)
      // Precision at K
      Array(1, 3, 5).foreach { k =>
        println(s"Precision at $k = ${metrics.precisionAt(k)}")
      }

      // Mean average precision
      println(s"Mean average precision = ${metrics.meanAveragePrecision}")

      // Normalized discounted cumulative gain
      Array(1, 3, 5).foreach { k =>
        println(s"NDCG at $k = ${metrics.ndcgAt(k)}")
      }

      // Get predictions for each data point
      val allPredictions = model.predict(ratings.map(r => (r.user, r.product))).map(r => ((r.user,
        r.product), r.rating))
      val allRatings = ratings.map(r => ((r.user, r.product), r.rating))
      val predictionsAndLabels = allPredictions.join(allRatings).map {
        case ((user, product),
          (predicted, actual)) =>
          (predicted, actual)
      }
      var alsMetric = scala.collection.mutable.Map[String, Double]()
      // Get the RMSE using regression metrics
      val regressionMetrics = new RegressionMetrics(predictionsAndLabels)
      println(s"RMSE = ${regressionMetrics.rootMeanSquaredError}")
      alsMetric.put("RMSE", regressionMetrics.rootMeanSquaredError)

      // R-squared
      println(s"R-squared = ${regressionMetrics.r2}")
      alsMetric.put("R-squared", regressionMetrics.r2)
      
     /* outputpathalsMetric.put(path2, alsMetric)
      ALSAttributes().setOutputpathalsMetric(outputpathalsMetric)

      outputpathModelLinkedHashMap.put(path3, model)
      ALSAttributes().setOutputpathModelLinkedHashMap(outputpathModelLinkedHashMap)*/

        var path="hdfs://localhost:8020/AlsOutput/UserResults"
        var path2="hdfs://localhost:8020/AlsOutput/UserResultOutput1"
        var count=userRecommendedDf.count()
        println( " no of row is  " )
        println(count)
        var counts= count/10;
        
        var df2=userRecommendedDf.limit(10)
        println( " new data frame   is 10 %%%%  "  )
        println(df2)
        
      writeToHDFS(sparkSession,userRecommendedDf,"true",path,path2)
    
  }
   def writeToHDFS(sparkSession: SparkSession, df_encoded: DataFrame, isHeaderAvailable: String, outputStorageDir: String, outPath: String): Unit = {
    println("inside hdfs")
     df_encoded.show()
     
     
   //  newdf = df_encoded.withColumn(("1","_2"), countfunc(col("_2"))).cache()
     
     //newdf = df_encoded.withColumn(_2, ratingfunc(col("features"), array(listOfcoefficients.map(lit(_)): _*))).cache()
    // newdf = newdf.select(col(customerId), col(itemId), col(ratingId))
    
    // println("  After count    ")
     
     //newdf.show()
     
     println(" upto count ")
     println( "   "  )
     println(" DataFrame show ")
     var sc = sparkSession.sparkContext
    val sqlContext = new SQLContext(sc)
    
      val conf = new Configuration()
      conf.set("fs.defaultFS", "hdfs://localhost:8020")
      val fs= FileSystem.get(conf)
      checkExistsAndDelete(outputStorageDir,fs)
    import sqlContext.implicits._
    val colArrayOfString = df_encoded.columns
    val len = colArrayOfString.length
    var rowString: String = ""
    for (i <- 0 to (len - 1)) {
      if (i != (len - 1)) {
        rowString = rowString + colArrayOfString(i) + ","
      } else {
        rowString = rowString + colArrayOfString(i)
      }

    }
    println(" row Strings    " + rowString)
    val rddheader = sc.parallelize(Seq(rowString)).map(_.toSeq.map(_.toString).mkString)
    val rdddf = df_encoded.rdd.map(_.toSeq.map(x => (if (x != null) { x.toString } else { x })).mkString(","))
    

    if (isHeaderAvailable == "true") {
      rddheader.union(rdddf).saveAsTextFile(outputStorageDir)
    } else if (isHeaderAvailable == "false") {
      rdddf.saveAsTextFile(outputStorageDir)
     
    }
    rdddf.foreach(println)
    println( " In RDD " )
  
   /* val wordcount = rdddf.map(line => (line.split("[")).map(_.trim))
    wordcount.foreach(println)
    var dfs= wordcount.toDF
    
    println(" rdd to dfs   ")
    dfs.show()*/
    var newdf: DataFrame = null
    newdf = df_encoded.withColumn("_2", countfunc(df_encoded.col("_1"),df_encoded.col("_2")))
     newdf.show()
    newdf = newdf.drop(col("_1"))
     newdf.show()
     newdf.rdd.saveAsTextFile(outPath)
     
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

}
