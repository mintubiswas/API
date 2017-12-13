package com.scala.spark

class AlsTest {
  /*var sparkSession = SparkSession.builder.master("local").appName("Spark SQL basic example").getOrCreate()
    println("in read data")
    val df = sparkSession.read.option("header","true").csv("hdfs://localhost:8020/Recomender/data.csv")
    //df.printSchema()
    df.show()
    println("mintu....")
    //df.select("productId").show()
   // val ratings = df.map(_.split(',') match {case Array(UserId, ItemId, Affinity) =>
     // Rating(UserId.toInt, ItemId.toInt, Affiity.toInt ) })
     // ratings.show()
*/    
  /*
      val conf = new Configuration()
      FileSystem.get(conf)
  
  
	      "alsRecommendationsOutputDir": "/Demo/SplitCollabClusters_ALS_Recommendation",
        "alsMetricStoragePath": "/Demo/SplitCollabClusters_ALS_Metric",
        "alsModelStorageOutputDir": "/Demo/SplitCollabClusters_ALS_Model",

//========================================================================================================

    val path: Path = new Path(hdfsPath)
    if (fs.exists(path)) {
      fs.delete(path, true)
    }
    val dataOutputStream: FSDataOutputStream = fs.create(path)
    val bw: BufferedWriter = new BufferedWriter(new OutputStreamWriter(dataOutputStream, "UTF-8"))
    bw.write(content)
    bw.close
  */
  
  
}