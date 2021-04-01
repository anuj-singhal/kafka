package com.anuj.edh.ingestor

import com.anuj.edh.oct.config.SparkAppConfig
import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails, GenericMessage}
import com.anuj.edh.oct.transform.FlightPaxDriver
import com.anuj.edh.oct.utils.CommonUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Assign
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.Duration
import org.slf4j.LoggerFactory
import com.anuj.edh.oct.config.{KafkaConfig, SparkAppConfig, ZookeeperConfig}
import com.anuj.edh.oct.transform.FlightPaxDriver
import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails, GenericMessage}
import com.anuj.edh.oct.scalaxb.FlightPax
import com.anuj.edh.oct.utils.CommonUtils
import org.apache.kafka.common.TopicPartition
import kafka.utils.ZkUtils
import org.I0Itec.zkclient.ZkClient
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode
import kafka.utils.ZKStringSerializer

import scala.collection.JavaConverters._

object FlightPaxIngestor extends App with SparkAppConfig{
  val logger = LoggerFactory.getLogger(getClass)
  //val readOffset = ZookeeperConfig.readOffsets(KafkaConfig.kafkaTopic)
  /*val stream = readOffset match {
    case Some(fromOffsets) =>
      KafkaUtils.createDirectStream[String, String](
        streamingContext,
        PreferConsistent,
        Assign[String, String](fromOffsets.keys.toList, KafkaConfig.kafkaParams, fromOffsets)
      )
    case None =>
      KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String]
        (KafkaConfig.kafkaArrayTopics, KafkaConfig.kafkaParams))
  }*/
  val kafkaParams = Map[String, Object](
    //"client.dns.lookup" -> "resolve_canonical_bootstrap_servers_only",
    "bootstrap.servers" -> "prod-worker-104fa71d.cloudera.etihad.net:9092, prod-worker-aff0fbb2.cloudera.etihad.net:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "flight_pax_hms",
    "sasl.kerberos.service.name" -> "kafka",
    "max.poll.interval.ms" -> "300000",
    "request.timeout.ms" -> "305000",
    "session.timeout.ms" -> "33000",
    "heartbeat.interval.ms" -> "10500",
    "max.partition.fetch.bytes" -> "5242880",
    "max.poll.records" -> "400",
    "auto.offset.reset" -> "earliest",
    //"auto.offset.reset" -> "latest",
    "security.protocol" -> "SASL_PLAINTEXT",
    "sasl.mechanism" -> "GSSAPI",
    //"auto.commit.interval.ms" -> kafkaConsumerConfig.get("auto.commit.interval.ms").get,
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  val kafkaArrayTopics: Array[String] = Array("flight_pax_hms");
  val kafkaTopic: String = "flight_pax_hms";
  var offsetRanges = Array.empty[OffsetRange]
  /*val stream = KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String]
    (KafkaConfig.kafkaArrayTopics, KafkaConfig.kafkaParams))*/
  val stream = KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String]
    (kafkaArrayTopics, kafkaParams))
  stream.transform { rdd =>
    offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    rdd
  }.foreachRDD { rddRecord =>
    for (o <- offsetRanges) {
      logger.info(s"Processing Offsets ---------------------- ${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
    }
    val rdd = rddRecord.map(x => x.value()).filter(_ != null)
    //val offsetRanges = rddRecord.asInstanceOf[HasOffsetRanges].offsetRanges
    val batchCount = rdd.count()
    logger.info(s"Total messages received in a batch: $batchCount")
    if (!rdd.isEmpty()) {
      //val offsetRanges = rddRecord.asInstanceOf[HasOffsetRanges].offsetRanges
      /*for (o <- offsetRanges) {
        logger.info(s"Processing Offsets ---------------------- ${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }*/
      val timer = System.currentTimeMillis() + " ms"
      val batchCount = rdd.count()
      logger.info(s"PROCESSING STARTED AT TIME : $timer INPUT RDD COUNT : $batchCount")
      val messageRdd = rdd.map(eachMessage => {
        try {
          val flightMessage = IngestionHelper.getMessageFromXML(eachMessage)
          val flightPax = FlightPaxDriver().processFlightPaxDetails(flightMessage)
          val flightPaxConnection = FlightPaxDriver().processFlightPaxConnectionDetails(flightMessage)
          val genericMessage = CommonUtils().getOriginalMessage(eachMessage, "Success")
          logger.info("Message Successfully processed...")
          (genericMessage, flightPax, flightPaxConnection)
        }
        catch{
          case e : Exception => {
            val flightPax = Seq(FlightPaxDetails())
            val flightPaxConnectionDetails = Seq(FlightPaxConnectionDetails())
            val genericMessage = GenericMessage(eachMessage,"Failed",e.getMessage)
            logger.info("Message Failed...")
            (genericMessage, flightPax, flightPaxConnectionDetails)
          }
        }
      })
      import spark.implicits._
      try {
        val path = "/raw/eag/ey/neo/flightpaxrawdata/" + CommonUtils().getCurrentDateWithHour
        logger.info(path)
        val genericMessageDF = messageRdd.map(x => x._1).toDF()
        genericMessageDF.coalesce(1).write.format("parquet").mode("append").save(path)
        //genericMessageDF.coalesce(1).write.format("Hive").mode(SaveMode.Append).insertInto("raw_ey_neo.flightpax")
        //genericMessageDF.show(10, false)
        //genericMessageDF.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save(s"C:/Work/Ethiad/flightpax/StreamingData/GenericMessage/$timer")
        val flightPaxDF = messageRdd.map(x => x._2).flatMap(x => x).toDF()
        flightPaxDF.coalesce(1).write.format("Hive").mode(SaveMode.Append).insertInto("raw_ey_neo.flightpax")
        //flightPaxDF.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save(s"C:/Work/Ethiad/flightpax/StreamingData/FlightPax/$timer")
        //flightPaxDF.show(10, false)
        val flightPaxConnectionDF = messageRdd.map(x => x._3).flatMap(x => x).toDF()
        flightPaxConnectionDF.coalesce(1).write.format("Hive").mode(SaveMode.Append).insertInto("raw_ey_neo.flightpaxconnection");
        //flightPaxConnectionDF.show(10, false)
        //flightPaxConnectionDF.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save(s"C:/Work/Ethiad/flightpax/StreamingData/FlightPaxConnection/$timer")
      }
      catch{
        case e: Throwable => {
          logger.error("Undefined exception: - " + e.printStackTrace())
        }
      }
      /*for (o <- offsetRanges) {
        logger.info(s"Commit Offsets ---------------------- ${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }*/
      //stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }
    else
      logger.info("------------------------------------------- RDD is Empty -----------------------------------------------")
  }
  for (o <- offsetRanges) {
    logger.info(s"Commit Offsets ---------------------- ${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
  }
  // Commit the kafka offsets in kafka after successful processing of every batch
  stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
  //ZookeeperConfig.saveOffsets(offsetRanges)
  streamingContext.start()
  streamingContext.awaitTermination()
}
