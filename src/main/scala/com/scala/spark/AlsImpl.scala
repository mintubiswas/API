/*package com.scala.spark
import scala.collection.mutable.HashMap
import scala.collection.immutable.Map
import org.apache.spark.sql.DataFrame
//import nt.nai.ml.util._
//import nt.nai.ml.model.dev._
class AlsImpl(_settings: HashMap[String, Any]) {

  var alsMetricName: String = _
  var alsNumFeatures: Int = _
  var alsTrainPercentage: Double = 0.7
  var alsTestPercentage: Double = 0.3
  var alsLambda = 0.01
  var alsNumOfRecommendations = 5
  var userId: String = ""
  var pipeLineId: String = ""
  var branchId: String = ""
  def setProperties(): Unit = {
    try {
      var unixTime = System.currentTimeMillis() / 1000L
    //  CommonProperties.setAlgoTimeStamp(unixTime)
     // userId = _settings.get(Constants.USER_ID).get.asInstanceOf[String]
     // pipeLineId = _settings.get(Constants.PIPELINE_ID).get.asInstanceOf[String]
     // branchId=_settings.get(Constants.BRANCH_ID).get.asInstanceOf[String]
      //    AlsProperties(userId,pipeLineId,branchId).setHyperParameterTunning(_settings.get(AlsProperties(userId,pipeLineId,branchId).ALS_HYPER_PARAMETER_TUNNING_JSON_PROPERTY).get.asInstanceOf[String])
      //    var kfold = _settings.get(HyperParameterTunningProperties.HT_KFOLD_JSON_PROPERTY).get.asInstanceOf[String]
      //    var kfolsInt = 0
      //    if (kfold != null && !(kfold.isEmpty())) {
      //      kfolsInt = kfold.toInt
      //    }
      //    HyperParameterTunningProperties.setKfold(kfolsInt)
      //    var seed = _settings.get(HyperParameterTunningProperties.HT_SEED_JSON_PROPERTY).get.asInstanceOf[String]
      //    var seedLongInt = 1L
      //    if (seed != null && !(seed.isEmpty())) {
      //      seedLongInt = seed.toLong
      //    }
      //    HyperParameterTunningProperties.setSeed(seedLongInt)
      //    var alsMetricNamedummy = _settings.get(AlsProperties(userId,pipeLineId,branchId).ALS_METRIC_NAME_JSON_PROPERTY).get.asInstanceOf[String]
      //    if (alsMetricNamedummy != null && !(alsMetricNamedummy.isEmpty())) {
      //      alsMetricName = alsMetricNamedummy
      //      AlsProperties(userId,pipeLineId,branchId).setAlsMetricName(alsMetricName)
      //    }
      var alsTrainPercentagedummy = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_TRAIN_PERCENTAGE_JSON_PROPERTY).get.asInstanceOf[String]
      if (alsTrainPercentagedummy != null && !(alsTrainPercentagedummy.isEmpty())) {
        alsTrainPercentage = alsTrainPercentagedummy.toDouble
        AlsProperties(userId, pipeLineId, branchId).setAlsTrainPercentage(alsTrainPercentage)
      }
      var alsNumOfRecommendationsdummy = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_NUM_RECOMMENDATIONS_JSON_PROPERTY).get.asInstanceOf[String]
      if (alsNumOfRecommendationsdummy != null && !(alsTrainPercentagedummy.isEmpty())) {
        alsNumOfRecommendations = alsNumOfRecommendationsdummy.toInt
        AlsProperties(userId, pipeLineId, branchId).setAlsNumOfRecommendations(alsNumOfRecommendations)
      }
      var alsLambdadummy = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_LAMBDA_JSON_PROPERTY).get.asInstanceOf[String]
      if (alsLambdadummy != null && !(alsLambdadummy.isEmpty())) {
        alsLambda = alsLambdadummy.toDouble
        AlsProperties(userId, pipeLineId, branchId).setAlsLambda(alsLambda)
      }
      var alsTestPercentagedummy = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_TEST_PERCENTAGE_JSON_PROPERTY).get.asInstanceOf[String]
      if (alsTestPercentagedummy != null && !(alsTestPercentagedummy.isEmpty())) {
        alsTestPercentage = alsTestPercentagedummy.toDouble
        AlsProperties(userId, pipeLineId, branchId).setAlsTestPercentage(alsTestPercentage)
      }
      //    var alsNumFeaturesString = _settings.get(AlsProperties(userId,pipeLineId,branchId).ALS_NUM_FEATURES_JSON_PROPERTY).get.asInstanceOf[String]
      //    if (alsNumFeaturesString != null && !(alsNumFeaturesString.isEmpty())) {
      //      alsNumFeatures = alsNumFeaturesString.toInt
      //      AlsProperties(userId,pipeLineId,branchId).setAlsNumFeatures(alsNumFeatures)
      //    }
      var alsNumIterationsString = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_NUM_ITERATIONS_JSON_PROPERTY).get.asInstanceOf[String]
      if (alsNumIterationsString != null && !(alsNumIterationsString.isEmpty())) {
        //alsMaxIterationsSeq = alsNumIterationsString.toInt
        AlsProperties(userId, pipeLineId, branchId).setAlsNumIterations(alsNumIterationsString)
      }
      var alsRankString = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_RANK_JSON_PROPERTY).get.asInstanceOf[String]
      if (alsRankString != null && !(alsRankString.isEmpty())) {
        //alsRank = alsRankString.toInt
        AlsProperties(userId, pipeLineId, branchId).setAlsRank(alsRankString)
      }
      
      var alsminRating = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_MIN_RATING).get.asInstanceOf[String]
      if (alsminRating != null && !(alsminRating.isEmpty())) {
        //alsMaxIterationsSeq = alsNumIterationsString.toInt
        AlsProperties(userId, pipeLineId, branchId).setMinRating(alsminRating)
      }
      
      var alsmaxRating = _settings.get(AlsProperties(userId, pipeLineId, branchId).ALS_MAX_RATING).get.asInstanceOf[String]
      if (alsmaxRating != null && !(alsmaxRating.isEmpty())) {
        //alsMaxIterationsSeq = alsNumIterationsString.toInt
        AlsProperties(userId, pipeLineId, branchId).setMaxRating(alsmaxRating)
      }
    } catch {

      case e: Exception =>
        {

       //   CommonUtil.exceptionCatch(e, "/" + userId + "/" + pipeLineId + "/" + branchId + "/" + "AlsImpl->")
        }

    }

    //    var regParamString = _settings.get(AlsProperties(userId,pipeLineId,branchId).ALS_REG_PARAM_JSON_PROPERTY).get.asInstanceOf[String]
    //    if (regParamString != null && !(regParamString.isEmpty())) {
    //      // regParam = alsRankString.toDouble
    //      AlsProperties(userId,pipeLineId,branchId).setRegParam(regParamString)
    //    }

  }
  def runAlgo(base_df: DataFrame): DataFrame = {
    println("Using als algo impl ")
    base_df.show()
    var outputDf: DataFrame = null
    setProperties()
    var alsObj = new AlsMl()
    var (df, alsModel) = alsObj.alsModelingPackage(base_df)
    outputDf = df
    outputDf
  }
  def runAlgoMultipleDfs(dfs: Seq[DataFrame]): Seq[DataFrame] = {
    println("Using affinity calc algo impl ")
    setProperties()
    var alsObj = new AlsMl()
   // var outputDfs = alsObj.alsProcessingMultipleDf(dfs)

    outputDfs
  }
}*/