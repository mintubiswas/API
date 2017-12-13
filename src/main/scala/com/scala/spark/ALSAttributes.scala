package com.scala.spark
import scala.collection.mutable.Map
import scala.beans.BeanProperty
import org.apache.spark.sql.DataFrame
import scala.collection.mutable.LinkedHashMap
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
class ALSAttributes private() {
 // @BeanProperty  var alsDf : DataFrame =_
  @BeanProperty  var outputpathalsMetric : LinkedHashMap[String,Map[String,Double]] =_
  @BeanProperty  var outputpathUserRecommendations : LinkedHashMap[String,DataFrame] =_
  @BeanProperty var outputpathModelLinkedHashMap: LinkedHashMap[String, MatrixFactorizationModel] = _
}
object ALSAttributes {
     private var _instance : ALSAttributes = null
  
     def apply() = {
    if (_instance == null)
      _instance = new ALSAttributes()
    _instance
  }  
  //def apply(uId: String,pId: String,bId: String) = new BiKmeansAttributes(uId,pId,bId)
}