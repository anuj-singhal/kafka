package com.anuj.edh.oct.config

import kafka.utils.ZkUtils
import net.ceedubs.ficus.Ficus._
import org.apache.kafka.common.TopicPartition
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.slf4j.LoggerFactory
//import scala.collection.JavaConverters._

// to be use in main
//val readOffset = ZookeeperConfig.readOffsets(KafkaConfig.kafkaTopic)
/*val stream = readOffset match {
  case Some(fromOffsets) =>
    KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Assign[String, String](fromOffsets.keys.toList.asJava, KafkaConfig.kafkaParams.asJava, fromOffsets.asJava)
    )
    KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent,
      Assign[String, String](fromOffsets.keys.toList.asJava, KafkaConfig.kafkaParams.asJava, fromOffsets.asJava))
 //KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](KafkaConfig.kafkaArrayTopics, KafkaConfig.kafkaParams))
  case None =>
    KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String]
      (KafkaConfig.kafkaArrayTopics, KafkaConfig.kafkaParams))
}*/

trait ZookeeperConfig extends ApplicationConfig {
  val logger = LoggerFactory.getLogger(getClass)
  private val consumerConfig = config.getConfig("kafka.consumer")
  private val zookeeperConfigParam = consumerConfig.as[Option[Map[String, String]]]("zookeeper")
    .getOrElse(Map(
      "zookeeperHost" -> "127.0.0.1:2181",
      "zookeeperPath" -> "/my_node/kafka/flight-pax-offset",
      "sessionTimeout" -> "10000",
      "connectionTimeout" -> "10000"
    ))

  private object zookeeperConfig {
    val zkHosts = zookeeperConfigParam.get("zookeeperHost").get
    val zkPath = zookeeperConfigParam.get("zookeeperPath").get
    val zkSessionTimeOut = zookeeperConfigParam.get("sessionTimeout").get.toInt
    val zkConnectionTimeOut = zookeeperConfigParam.get("connectionTimeout").get.toInt
  }

  private object zkUtils {
    val zkClient = ZkUtils.apply(zookeeperConfig.zkHosts, zookeeperConfig.zkSessionTimeOut, zookeeperConfig.zkConnectionTimeOut, false)
  }

  def readOffsets(topic : String) = {
    logger.info("Reading offsets from ZooKeeper")
    val timer = System.currentTimeMillis()
    val (offsetsRangesStrOpt, _) = zkUtils.zkClient.readDataMaybeNull(zookeeperConfig.zkPath)
    offsetsRangesStrOpt match {
      case Some(offsetsRangesStr) =>
        logger.debug(s"Read offset ranges: ${offsetsRangesStr}")
        val offsets = offsetsRangesStr.split(",")
          .map(s => s.split(":"))
          .map { case Array(topicNameStr, partitionStr, offsetStr) =>
            new TopicPartition(topic, partitionStr.toInt) -> offsetStr.toLong//java.lang.Long.valueOf(offsetStr)
          }.toMap
        logger.info("Done reading offsets from ZooKeeper. Took " + (System.currentTimeMillis() - timer) + " ms")
        Some(offsets)
      case None =>
        logger.info("No offsets found in ZooKeeper. Took " + (System.currentTimeMillis() - timer) + " ms")
        None
    }
  }

  def saveOffsets(rdd : RDD[_]) : Unit = {
    logger.info("Saving offsets to ZooKeeper")
    val timer = System.currentTimeMillis()
    val offsetsRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    offsetsRanges.foreach(offsetRange => logger.debug(s"Using ${offsetRange} ${offsetRange.topic} ${offsetRange.partition} ${offsetRange.fromOffset} ${offsetRange.untilOffset}"))
    val offsetsRangesStr = offsetsRanges.map(offsetRange => s"${offsetRange.topic}:${offsetRange.partition}:${offsetRange.fromOffset}")
      .mkString(",")
    logger.debug(s"Writing offsets to ZooKeeper: ${offsetsRangesStr}")
    zkUtils.zkClient.updatePersistentPath(zookeeperConfig.zkPath, offsetsRangesStr)
    logger.info("Done updating offsets in ZooKeeper. Took " + (System.currentTimeMillis() - timer) + " ms")
  }

  def saveOffsets(offsetsRanges : Array[OffsetRange]) : Unit = {
    val timer = System.currentTimeMillis()
    logger.info("Saving offsets to ZooKeeper using offsetRange")
    offsetsRanges.foreach(offsetRange => logger.debug(s"Using ${offsetRange} ${offsetRange.topic} ${offsetRange.partition} ${offsetRange.fromOffset} ${offsetRange.untilOffset}"))
    val offsetsRangesStr = offsetsRanges.map(offsetRange => s"${offsetRange.topic}:${offsetRange.partition}:${offsetRange.fromOffset}")
      .mkString(",")
    logger.debug(s"Writing offsets to ZooKeeper: ${offsetsRangesStr}")
    zkUtils.zkClient.updatePersistentPath(zookeeperConfig.zkPath, offsetsRangesStr)
    logger.info("Done updating offsets in ZooKeeper. Took " + (System.currentTimeMillis() - timer) + " ms")
  }
}
object ZookeeperConfig extends ZookeeperConfig
