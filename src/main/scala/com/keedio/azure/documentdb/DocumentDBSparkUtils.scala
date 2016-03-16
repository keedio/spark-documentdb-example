package com.keedio.azure.documentdb

import com.microsoft.azure.documentdb.Document
import com.microsoft.azure.documentdb.hadoop.{DocumentDBOutputFormat, DocumentDBWritable}
import evo.insight.common.IdTrait
import org.apache.hadoop.io.{Text, Writable}
import org.apache.spark.rdd.RDD
import org.json.JSONObject

/**
 * Useful utils to persist from spark to Azure Document DB.
 *
 * Created by Luca Rosellini <lrosellini@keedio.com> on 18/12/15.
 */
object DocumentDBSparkUtils {
  implicit def documentDBSparkUtils[K, V](rdd: RDD[(K, V)]): PairRDDDocumentDBSparkUtils[K, V] = new PairRDDDocumentDBSparkUtils(rdd)
  implicit def objectRDDdocumentDBSparkUtils[T <: IdTrait](rdd: RDD[T]): ObjectRDDDocumentDBSparkUtils[T] = new ObjectRDDDocumentDBSparkUtils(rdd)
}


/**
 * Wrapper for key/value RDDs to add saveToDocumentDB functionality.
 *
 * @param rdd the RDD to save.
 * @tparam K type of the key.
 * @tparam V type of the value.
 */
class PairRDDDocumentDBSparkUtils[K, V](val rdd: RDD[(K, V)]) {

  /**
   * Persists this RDD to DocumentDB. In order to persist to DocumentDB, we need a function
   * to map the key type K to an hadoop Writable object. It also needs an additional function
   * used to map the value to a JSONObject (that will be converted internally to DocumentDBWriteable).
   *
   * @param f1 a mapping function from K to org.apache.hadoop.io.Writable
   * @param f2 a mapping function from V to org.json.JSONObject
   * @param conf documentDB configuration (instance of com.keedio.azure.documentdb.DocumentDBConf).
   * @return
   */
  def saveToDocumentDB(f1: K => Writable,
                       f2: ((K, V)) => JSONObject)(implicit conf: DocumentDBConf): Unit = {

    val readyToWrite: RDD[(Writable, DocumentDBWritable)] =
      rdd.map((e: (K, V)) => (f1(e._1), new DocumentDBWritable(new Document(f2(e)))))

    val keyClass = classOf[Writable]
    val valueClass = classOf[DocumentDBWritable]
    val outputFormatClass = classOf[DocumentDBOutputFormat]

    readyToWrite.saveAsNewAPIHadoopFile("", classOf[Writable], classOf[DocumentDBWritable], classOf[DocumentDBOutputFormat], conf.getHadoopConf)
  }
}

/**
 * Wrapper for key/value RDDs to add saveToDocumentDB functionality.
 *
 * @param rdd the RDD to save.
 * @tparam K type of the rdd objects.
 */
class ObjectRDDDocumentDBSparkUtils[K <: IdTrait](val rdd: RDD[K]) {

  /**
   * Persists this RDD to DocumentDB..

   * @param objToJsonStr a converter function fron K to String (representing a Json).
   * @param conf documentDB configuration (instance of com.keedio.azure.documentdb.DocumentDBConf).
   * @return
   */
  def saveToDocumentDB(objToJsonStr: (K => String))(implicit conf: DocumentDBConf): Unit = {
    import com.github.wnameless.json.flattener.JsonFlattener._
    import org.json4s._

    implicit val formats = DefaultFormats

    val jsonRdd = rdd map { e => (e.uuid, flatten(objToJsonStr(e))) }

    val readyToWrite: RDD[(Writable, DocumentDBWritable)] =
      jsonRdd map{ t => (new Text(t._1), new DocumentDBWritable(new Document(t._2))) }

    val keyClass = classOf[Writable]
    val valueClass = classOf[DocumentDBWritable]
    val outputFormatClass = classOf[DocumentDBOutputFormat]

    readyToWrite.saveAsNewAPIHadoopFile("", classOf[Writable], classOf[DocumentDBWritable], classOf[DocumentDBOutputFormat], conf.getHadoopConf)
  }
}