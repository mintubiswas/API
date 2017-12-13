package nt.nai.services
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.sql.DataFrame
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
object MainLift {
  def main(args: Array[String])
  {
    var sparkSession = SparkSession.builder.master("local").appName("Als Recommender").getOrCreate()
    println("in read data")
    var sc=sparkSession.sparkContext
    val df = sparkSession.read.option("header","true").option("inferSchema", true).csv("hdfs://localhost:8020/data/lift2/credit_defaults.csv")
    /*val spark=SparkSession.builder.master("local").appName(" Spark SQL basic example ").getOrCreate()
    var sc=spark.sparkContext
  // val df = spark.read.csv("hdfs://localhost:8020//data/lift2/credit_defaults.csv")
    //val df = spark.read.csv("/home/mintu-biswas/Downloads/Baby_Names.csv")
    val df = spark.read.csv("hdfs://localhost:8020/data/CollectionData.csv")*/
   // df.show()
   /*var colss="MonthlyIncome"
   var cols = colss.toInt*/
   // var salary = df.orderBy(asc("MonthlyIncome"))
   // salary.select("MonthlyIncome").show(40)
    val distinctValuesDF = df.select(df("EDUCATION")).distinct
    val dfs= df.groupBy("EDUCATION").count()
    dfs.show()
   //   println("  count is  " + distinctValuesDF)
     //var count= distinctValuesDF.count()
     // println( "  count is ==>  "+  count)
     // distinctValuesDF.show()
    /*val list:List[Any]= distinctValuesDF.select("SeriousDlqin2yrs").collect().map(_(0)).toList 
    
    var counts=  df.select("MonthlyIncome").count
   println( " counts value is   " +counts)
    //df.printSchema()
    df.select("MonthlyIncome").show(40)
   val tempList= df.stat.approxQuantile("MonthlyIncome", Array(0.25,0.5,0.75), 0.0).toList
     tempList.foreach(println)
//     val outliers = df.filter(df.col("MonthlyIncome") > highLimit)
  //  outliers.show()
*/
     
     
   /*val splits:Array[DataFrame] = df.randomSplit(Array(0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1))
   val store:Array[DataFrame] = new Array[DataFrame](20)
    val dfs: DataFrame=null
    for(i <- 0 to 9)
    {
     store(i) = splits(i)
      
    store(i)= store(i).groupBy("_c3").count()
      
      //store(i).show()
      
    }
    store(10)=store(0)
    for(i <- 1 to 9)
    {
       store(10)=store(10).union(store(i))
    }
    println(" final  ")
    //store(10).show()
     val dd = store(10)*/
    
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     // dd.show()
     
     
     /* df.show()
    df.printSchema()
    import spark.implicits._
    df.select("_c0").show()
    df.select($"_c0", $"_c1" +1).show()
    df.filter($"_c0" > 1000).show()
    df.groupBy("_c1").count().show()
    
    df.createOrReplaceTempView("RandomForest")
    val sqlDF = spark.sql("SELECT * FROM RandomForest")
   // sqlDF.show()
   // df.createGlobalTempView("Forest")
   // spark.sql("SELECT * FROM global_temp.Forest").show()
    
    case class Person(name: String, age: Int)
    //val ds = Seq(Person("Andy", 32)).toDS()
    //ds.show
    val pdf= Seq(1,2,3).toDS()
    pdf.map(_ + 1).collect()*/
    
    
    
    
    
  /*  val peopleDF= spark.sparkContext.textFile("/home/mintu-biswas/Downloads/Baby_Names.csv")
                  .map(_.split(","))
                  .map(att => Person(att(2),att(1).trim.toInt)).toDF()
                  
     peopleDF.createOrReplaceTempView("people")
     
     val teenDf = spark.sql("SELECT name, age FROM people")
     
     teenDf.map(teen => "Name:  " +teen(0)).show()
     
     implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
    
    teenDf.map(teen => teen.getValuesMap[Any](List("name", "age"))).collect()*/
    
    
    
    
  }
}