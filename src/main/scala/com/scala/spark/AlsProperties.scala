package com.scala.spark
import scala.beans.BeanProperty
import scala.collection.mutable.LinkedHashMap
class AlsProperties private (private val uId: String, private val pId: String, private val bId: String) {
  val PRODUCT_AFFINITY_DELIMITER: String = "productAffinityDelimiter"
  val PRODUCT_WITH_AFFINITY_DELIMITER: String = "productWithAffinityDelimiter"
  val ALS_NUM_FEATURES_JSON_PROPERTY: String = "alsNumFeatures"
  val ALS_RANK_JSON_PROPERTY: String = "alsRank"
  val ALS_RANK_RANGE_JSON_PROPERTY: String = "rankRange"
  val ALS_NUM_ITERATIONS_JSON_PROPERTY: String = "alsNumIterations"
  val ALS_RECOMMENDATIONS_OUTPUT_DIR_JSON_PROPERTY: String = "alsRecommendationsOutputDir"
  val ALS_INPUT_FILE_JSON_PROPERTY: String = "alsInputFile"
  val ALS_NUM_RECOMMENDATIONS_JSON_PROPERTY: String = "alsNumOfRecommendations"
  val ALS_MIN_RATING: String = "minRating"
  val ALS_MAX_RATING: String = "maxRating"
  val ALS_LAMBDA_JSON_PROPERTY: String = "alsLambda"
  val ALS_FEATURES_DELIMITER_JSON_PROPERTY: String = "alsFeaturesDelimiter"
  val ALS_TRAIN_PERCENTAGE_JSON_PROPERTY: String = "alsTrainPercentage"
  val ALS_TEST_PERCENTAGE_JSON_PROPERTY: String = "alsTestPercentage"
  val HDFS_URI_JSON_PROPERTY: String = "hdfsUri"
  val ALS_NON_FEATURES_COL_LIST_JSON_PROPERTY = "alsNonFeaturesColsList"
  val ALS_METRIC_STORAGE_PATH_JSON_PROPERTY: String = "alsMetricStoragePath"
  val ALS_MODEL_STORAGE_OUTPUT_DIR_JSON_PROPERTY: String = "alsModelStorageOutputDir"
  val ALS_COEFFICIENT_STORAGE_OUTPUT_DIR_JSON_PROPERTY = "alsCoefficientStorageOutputDir"
  val ALS_OUTPUT_IN_ELASTICSEARCH_JSON_PROPERTY: String = "alsOutputInElasticsearch"
  val ALS_OUTPUT_ELASTICSEARCH_INDEX_JSON_PROPERTY: String = "alsOutputElasticsearchIndex"
  val ALS_OUTPUT_ELASTICSEARCH_USER_JSON_PROPERTY: String = "alsOutputElasticsearchUser"
  val ALS_OUTPUT_ELASTICSEARCH_PASSWORD_JSON_PROPERTY: String = "alsOutputElasticsearchPassword"
  val ALS_HYPER_PARAMETER_TUNNING_JSON_PROPERTY: String = "hyperParameterTunning"
  //val ALS_USER_COL_JSON_PROPERTY: String ="commonUseridCol"
  //val ALS_ITEM_COL_JSON_PROPERTY: String ="commonItemidCol"
  //val ALS_RATING_COL_JSON_PROPERTY: String ="ratingCol"
  val ALS_REG_PARAM_JSON_PROPERTY: String = "regParam"
  val ALS_MAX_LOOP_JSON_PROPERTY: String = "alsMaxLoop"
  val ALS_METRIC_NAME_JSON_PROPERTY: String = "alsMetricName"
  val ALS_KFOLD_JSON_PROPERTY = "kfold"
  val ALS_ALGONAME_JSON_PROPERTY = "algoName"
  val ALS_SEED_JSON_PROPERTY = "seed"

  @BeanProperty var kfold: Int = _
  @BeanProperty var seed: Long = _
  @BeanProperty var algoName: String = _
  @BeanProperty var hyperParameterTunning: String = _
  @BeanProperty var alsMetricName: String = _
  @BeanProperty var productAffinityDelimiter: String = _
  @BeanProperty var productWithAffinityDelimiter: String = _
  @BeanProperty var alsModelStorageOutputDir: String = _
  @BeanProperty var alsMetricStoragePath: String = _
  @BeanProperty var alsCoefficientStorageOutputDir: String = _
  @BeanProperty var alsRank: String = _
  @BeanProperty var rankRange: Int = _
  @BeanProperty var alsNumFeatures: Int = _
  @BeanProperty var alsNumIterations: String = _
  @BeanProperty var alsRecommendationsOutputDir: String = _
  @BeanProperty var alsInputFile: String = _
  @BeanProperty var alsNumOfRecommendations: Int = _
  @BeanProperty var alsLambda: Double = _
  @BeanProperty var alsFeaturesDelimiter: String = _
  @BeanProperty var alsNonFeaturesColsList: String = _
  @BeanProperty var alsTrainPercentage: Double = _
  @BeanProperty var alsTestPercentage: Double = _
  @BeanProperty var hdfsUri: String = _
  @BeanProperty var alsOutputInElasticsearch: String = _
  @BeanProperty var alsOutputElasticsearchIndex: String = _
  @BeanProperty var alsOutputElasticsearchUser: String = _
  @BeanProperty var alsOutputElasticsearchPassword: String = _
  //@BeanProperty  var commonUseridCol : String =_
  //@BeanProperty  var commonItemidCol : String =_
  //@BeanProperty  var ratingCol : String =_
  @BeanProperty var regParam: String = _
  @BeanProperty var alsMaxLoop: Int = _
  @BeanProperty var minRating: String = _
  @BeanProperty var maxRating: String = _
  override def toString =
    {
      this.uId + this.pId + this.bId
    }
}

object AlsProperties {
  private var _instance: AlsProperties = null
  private var mapOfObjects: LinkedHashMap[String, AlsProperties] = new LinkedHashMap[String, AlsProperties]
  // DecisionTreeProperties.
  def apply(uId: String, pId: String, bId: String) = {
    if (_instance == null || _instance.toString() != uId + pId + bId) {
      println("als object is created " + uId + pId + bId)
      if (mapOfObjects.contains(uId + pId + bId)) {
        _instance = mapOfObjects.get(uId + pId + bId).get
      } else {
        _instance = new AlsProperties(uId, pId, bId)
      }
      mapOfObjects.put(uId + pId + bId, _instance)
    }
    _instance
  }
  //def apply(uId: String,pId: String,bId: String) = new DecisionTreeProperties(uId,pId,bId)
}
