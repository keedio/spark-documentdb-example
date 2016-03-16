package com.keedio.azure.documentdb.example

import java.util.UUID

import com.keedio.azure.documentdb.{DocumentDBSparkUtils, DocumentDBConf}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.LazyLogging
import evo.insight.common.{Category, Payment}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import muster.codec.jawn.api._
import DocumentDBSparkUtils._

import scala.util.Random

/**
 * Tests writing an example file to DocumentDB and reading data back.
 *
 * Created by Luca Rosellini <lrosellini@keedio.com> on 18/12/15.
 */
object SaveToDocumentDbObjectRDDExample extends App with LazyLogging {
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

  val payments =
    for (i <- 1 to 10000) yield {
      Payment(UUID.randomUUID().toString,
        Random.nextString(9), Random.nextString(10), System.currentTimeMillis(), Random.nextFloat(), Random.nextString(20),
        Category(Random.nextInt(), Random.nextString(15)))
    }

  val rdd: RDD[Payment] = sc.parallelize(payments)


  rdd.saveToDocumentDB({e: Payment => e.asJson})
}

