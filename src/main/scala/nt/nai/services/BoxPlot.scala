package nt.nai.services
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.sql.DataFrame
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import scala.collection.mutable.{Map => MutableMap}
class BoxPlot {
  
  def boxplot(df:DataFrame, fCol:String,Lcol:String):scala.collection.mutable.Map[String,Array[Double]]=
  {
    val featuresCol: Boolean= true
    var firstQuantile:Double = 0
    var secondQuantile:Double = 0
    var thirdQuantile:Double = 0
    var tempList:List[Double]= null
    var IQR:Double=0
    var lowLimit:Double = 0
    var highLimit:Double =0 
    var OutputMap = scala.collection.mutable.Map[String,Array[Double]]()
    if(featuresCol)
    {
      val distinctValuesDF = df.select(df(fCol)).distinct
      //distinctValuesDF.show()
      val list:List[Any]= distinctValuesDF.select(fCol).collect().map(_(0)).toList 
    for(i <- 0 to list.size-1)
    {
      var OutArray:Array[Double]=new Array[Double](10)
      val feature=list(i)
     
      val countF=df.filter(df.col(fCol) === feature).count()
      if(countF >5)
      {
     val dfs:DataFrame=df.filter(df.col(fCol) === feature)
     
     tempList= dfs.stat.approxQuantile(Lcol, Array(0.25,0.5,0.75), 0.0).toList
     firstQuantile = tempList(0)
     secondQuantile = tempList(1)
     thirdQuantile = tempList(2)
    
     IQR = thirdQuantile - firstQuantile
    
     lowLimit = firstQuantile - (1.5 * IQR)
     highLimit = thirdQuantile + (1.5 * IQR)
  
    val count1=dfs.count()
    val outliers1 = dfs.filter(dfs.col(Lcol) > highLimit).toDF()
    val outliers2 = dfs.filter(dfs.col(Lcol) < lowLimit).toDF()
    
    val fTable1 = dfs.filter(dfs.col(Lcol) > lowLimit).toDF()
    val fTable2 = fTable1.filter(fTable1.col(Lcol) < highLimit).toDF()
  
    val highPoint=fTable2.select(max(fTable2.col(Lcol)))
    val lowPoint = fTable2.select(min(fTable2.col(Lcol)))
    
    val highPoint2=highPoint.head().getInt(0)
    val lowPoint2 = lowPoint.head().getInt(0)
    
    OutArray(0)=lowPoint2
    OutArray(1)=firstQuantile
     OutArray(2)=secondQuantile
     OutArray(3)=thirdQuantile
     OutArray(4)=highPoint2
  
     OutputMap.put(feature.toString, OutArray)
   
      }
      else
      {
        println(" features is too small ")
      }
      }
    }
    else
    {
      var keye="nonFeatures"
       var OutArray:Array[Double]=new Array[Double](10)
       val countF=df.count()
      if(countF >5)
      {
     tempList= df.stat.approxQuantile(Lcol, Array(0.25,0.5,0.75), 0.0).toList
     firstQuantile = tempList(0)
     secondQuantile = tempList(1)
     thirdQuantile = tempList(2)
    
     IQR = thirdQuantile - firstQuantile
    
     lowLimit = firstQuantile - (1.5 * IQR)
     highLimit = thirdQuantile + (1.5 * IQR)
   
    val outliers1 = df.filter(df.col(Lcol) > highLimit).toDF()
    val outliers2 = df.filter(df.col(Lcol) < lowLimit).toDF()
    
    val fTable1 = df.filter(df.col(Lcol) > lowLimit).toDF()
    val fTable2 = fTable1.filter(fTable1.col(Lcol) < highLimit).toDF()
  
    val highPoint=fTable2.select(max(fTable2.col(Lcol)))
    val lowPoint = fTable2.select(min(fTable2.col(Lcol)))
    
    val highPoint2=highPoint.head().getInt(0)
    val lowPoint2 = lowPoint.head().getInt(0)
     OutArray(0)=lowPoint2
    OutArray(1)=firstQuantile
     OutArray(2)=secondQuantile
     OutArray(3)=thirdQuantile
     OutArray(4)=highPoint2
     OutputMap.put(keye, OutArray)
     
      }
      else
      {
        println(" To short data set ")
      }
       
    }
    OutputMap
   }
}