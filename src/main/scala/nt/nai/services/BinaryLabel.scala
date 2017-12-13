/*package nt.nai.services

class BinaryLabel {
  
  //  println("args(0)="+ args(0))
  //  println("args(1)=" +args(1))
//"sourceSettings": [
//    {
//      "algo": "HDFS",
//      "dataSource": "hdfs",
//      "dataHeaderPresent": "true",
//      "Delimiter": ",",
//      "inputFilePath": "hdfs://localhost:9000/Romil/SeriousDlqin2yrsDataOfTelco_10k.csv",
//      "nonFeaturesColsList": "State",
//      "hdfsUri": "hdfs://localhost:9000",
//      "hiveWarehouseDir": "/user/hive/warehouse",
//      "csv": "no",
//      "sourceCategory": "primary",
//      "algoName": "NA"
//    }
//  ],
    
  //   if (args(0).equalsIgnoreCase(Constants.Pipeline)) {
      var input="""{
        "etlSettings": [],
  "variableDerivationSettings": [],
  "dataExplorationSettings": [],
  "modelAlgoSettings": [
    [
      {
        "algo": "Random Forest Classification",
        "commonLableCol": "default_payment_next_month",
        "rfModelStoragePath": "/multimdl/6/randomforestClassification/model",
        "rfHyperParameterTunning": "false",
        "kfold": "4",
        "seed": "12345",
        "rfTrainPercentage": "0.7",
        "rfTestPercentage": "0.3",
        "rfMaxDepth": "5",
        "rfMaxBins": "5",
        "rfFeatureSubsetStrategy": "auto",
        "rfImpurity": "gini",
        "rfMinimumInfoGain": "0.01",
        "rfNumTree": "3",
        "rfMinimumInstancePerNode": "4",
        "rfSubSamplingRate": "0.1",
        "rfIsCacheNodeIds": "false",
        "rfCheckpointInterval": "-1",
        "rfIsMultiClass": "false",
        "rfMetricName": "areaUnderROC",
        "groupName": "modelAlgoSettings",
        "viewIntermediate": "false",
        "algoType": "RandomForestClassification",
        "dragId": 3,
        "algoText": "rf-c",
        "hdfsUri": "hdfs://localhost:8020",
        "branchId": "b1",
        "sinkType": "hdfs",
        "algoName": "RandomForestClassification"
      }
    ]
  ],
  "dataProfilingSettings": [
  {"algo":"Bucketizer",
  "splits":[{"split":"1,2","bucketizercol":"age",
  "algoid":"b","maxBound":"","minBound":""}],
  "bucketizercol":"age",
  "viewIntermediate":"true",
  "outputStorageDir":"",
  "groupName":"dataProfilingSettings",
  "algoType":"Bucketizer",
  "dragId":2,
  "algoText":"b",
  "hdfsUri":"hdfs://localhost:8020"}
  ],
  
  "sourceSettings":[
  {"algo":"HDFS",
  "dataSource":"hdfs",
  "dataHeaderPresent":"true",
  "Delimiter":",",
  "inputFilePath":"hdfs://localhost:8020//data/lift2/credit_defaults.csv",
  "nonFeaturesColsList":"",
  "hdfsUri":"hdfs://localhost:8020",
  "hiveWarehouseDir":"/user/hive/warehouse",
  "csv":"no","sourceCategory":
  "primary","algoName":"NA",
   "DataframeInputId":"D1"
  }],
  "sinkSettings": [
    [
      {
        "algo": "Random Forest Classification",
        "commonLableCol": "default_payment_next_month",
        "rfModelStoragePath": "hdfs://localhost:8020//AlsOutput/output",
        "rfHyperParameterTunning": "false",
        "kfold": "4",
        "seed": "12345",
        "rfTrainPercentage": "0.7",
        "rfTestPercentage": "0.3",
        "rfMaxDepth": "5",
        "rfMaxBins": "5",
        "rfFeatureSubsetStrategy": "auto",
        "rfImpurity": "gini",
        "rfMinimumInfoGain": "0.01",
        "rfNumTree": "3",
        "rfMinimumInstancePerNode": "4",
        "rfSubSamplingRate": "0.1",
        "rfIsCacheNodeIds": "false",
        "rfCheckpointInterval": "-1",
        "rfIsMultiClass": "false",
        "rfMetricName": "areaUnderROC",
        "groupName": "modelAlgoSettings",
        "viewIntermediate": "false",
        "algoType": "RandomForestClassification",
        "dragId": 3,
        "algoText": "rf-c",
        "hdfsUri": "hdfs://localhost:8020",
        "branchId": "b1",
        "sinkType": "hdfs",
        "algoName": "RandomForestClassification"
      }
    ]
  ],
  "timestamp": "98765",
  "pipelineId": "12",
  "userID": "india",
  "ElasticIndex": "in1",
  "ElasticIP": "localhost:9300",
  "ElasticCluster": "my-application",
  "ElasticClusterNode": "node-1",
  "user": "mintu-biswas",
  "password": "biswas",
  "port": "3306",
  "ip": "localhost",
  "database": "mysql",
  "commonHdfsUri":"hdfs://localhost:8020",
  "tableForApplicationId": "xuInfo"
}"""
      JsonFileParser.parseRootJson(input)
      val pipelineExec = new PipeLineExecute()
      pipelineExec.runPipeline()

  //  } 
}*/