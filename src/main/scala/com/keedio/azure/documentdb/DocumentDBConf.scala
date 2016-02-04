package com.keedio.azure.documentdb

import com.microsoft.azure.documentdb.hadoop.ConfigurationUtil
import org.apache.commons.lang.StringUtils
import org.apache.hadoop.conf.Configuration

/**
 * Generic configuration class.
 *
 * Created by Luca Rosellini <lrosellini@keedio.com> on 18/12/15.
 */
case class DocumentDBConf(docDBHost: String, docDbDbName: String, docDbOutputCollection: String, docDbKey: String){
  require(StringUtils.isNotEmpty(docDBHost),"'azure.documentdb.host' argument cannot be empty or null")
  require(StringUtils.isNotEmpty(docDbDbName),"'azure.documentdb.dbname' argument cannot be empty or null")
  require(StringUtils.isNotEmpty(docDbOutputCollection),"'azure.documentdb.collection.output' argument cannot be empty or null")
  require(StringUtils.isNotEmpty(docDbKey),"'azure.documentdb.key' argument cannot be empty or null")

  def getHadoopConf(): Configuration ={
    val outConf = new Configuration()

    outConf.set(ConfigurationUtil.DB_NAME, docDbDbName)
    outConf.set(ConfigurationUtil.OUTPUT_COLLECTION_NAMES, docDbOutputCollection)
    outConf.set(ConfigurationUtil.DB_KEY, docDbKey)
    outConf.set(ConfigurationUtil.DB_HOST, docDBHost)

    outConf
  }
}
