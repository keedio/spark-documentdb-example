package com.keedio.azure.documentdb.example

import com.keedio.azure.documentdb.{DocumentDBConf, DocumentDBSparkUtils}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.hadoop.io.Text
import org.apache.spark.rdd.RDD._
import org.apache.spark.{SparkConf, SparkContext}
import org.json.JSONObject

/**
 * Tests writing an example file to DocumentDB and reading data back.
 *
 * Created by Luca Rosellini <lrosellini@keedio.com> on 18/12/15.
 */
object SaveToDocumentDbPairRDDExample extends App with LazyLogging {
  val config = ConfigFactory.load()

  implicit val ddbConf = DocumentDBConf(
    config.getString("azure.documentdb.host"),
    config.getString("azure.documentdb.dbname"),
    config.getString("azure.documentdb.collection.output"),
    config.getString("azure.documentdb.key"))

  logger.info(s"configuration: $ddbConf")

  val sparkConfig = new SparkConf()
  sparkConfig.setMaster("local")
  sparkConfig.setAppName("com.keedio.azure.documentdb.example.SaveToDocumentDbTest")

  val sc: SparkContext = new SparkContext(sparkConfig)

  val file = sc.textFile("pg1012.txt")

  val counts = file.flatMap(line => line.split(" "))
    .map(word => (word, 1))
    .reduceByKey(_ + _)

  logger.info(s"read ${counts.count()} lines from input file")

  import DocumentDBSparkUtils._

  /*
   * for each word sends to DocumentDB a simple JSON representing the word with the associated count.
   */
  counts.saveToDocumentDB({
    new Text(_)

  }, { t: (String, Int) => {
    val jobj = new JSONObject
    jobj.put("word", t._1)
    jobj.put("count", t._2)
    jobj
  }
  })



}

