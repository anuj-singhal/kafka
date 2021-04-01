package com.anuj.edh.ingestor

import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails, GenericMessage}
import com.anuj.edh.oct.transform.FlightPaxDriver
import com.anuj.edh.oct.utils.CommonUtils
import com.anuj.edh.oct.config.SparkAppConfig
import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails, GenericMessage}
import com.anuj.edh.oct.utils.CommonUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.slf4j.LoggerFactory

object FlightPaxIngestorNew {
  val logger = LoggerFactory.getLogger(getClass)
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("FlightPax-Streaming").setMaster("yarn")
    val streamingContext: StreamingContext = new StreamingContext(sparkConf, Duration(args(3).toInt))
    val spark = SparkSession.builder.config(streamingContext.sparkContext.getConf)
      .config("hive.exec.dynamic.partition", "true")
      .config("hive.exec.dynamic.partition.mode", "nonstrict").enableHiveSupport()
      .getOrCreate()

    val kafkaParams = Map[String, Object](
      //"client.dns.lookup" -> "resolve_canonical_bootstrap_servers_only",
      "bootstrap.servers" -> args(2),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> args(0),
      "sasl.kerberos.service.name" -> "kafka",
      "max.poll.interval.ms" -> "300000",
      "request.timeout.ms" -> "305000",
      "session.timeout.ms" -> "33000",
      "heartbeat.interval.ms" -> "10500",
      "max.partition.fetch.bytes" -> "5242880",
      "max.poll.records" -> "400",
      "auto.offset.reset" -> args(1),
      //"auto.offset.reset" -> "latest",
      "security.protocol" -> "SASL_PLAINTEXT",
      "sasl.mechanism" -> "GSSAPI",
      //"auto.commit.interval.ms" -> kafkaConsumerConfig.get("auto.commit.interval.ms").get,
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )val kafkaParams = Map[String, Object](
      //"client.dns.lookup" -> "resolve_canonical_bootstrap_servers_only",
      "bootstrap.servers" -> args(2),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> args(0),
      "sasl.kerberos.service.name" -> "kafka",
      "max.poll.interval.ms" -> "300000",
      "request.timeout.ms" -> "305000",
      "session.timeout.ms" -> "33000",
      "heartbeat.interval.ms" -> "10500",
      "max.partition.fetch.bytes" -> "5242880",
      "max.poll.records" -> "400",
      "auto.offset.reset" -> args(1),
      //"auto.offset.reset" -> "latest",
      "security.protocol" -> "SASL_PLAINTEXT",
      "sasl.mechanism" -> "GSSAPI",
      //"auto.commit.interval.ms" -> kafkaConsumerConfig.get("auto.commit.interval.ms").get,
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val kafkaArrayTopics: Array[String] = Array(args(0));
    val kafkaTopic: String = args(0);
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
          catch {
            case e: Exception => {
              val flightPax = Seq(FlightPaxDetails())
              val flightPaxConnectionDetails = Seq(FlightPaxConnectionDetails())
              val genericMessage = GenericMessage(eachMessage, "Failed", e.getMessage)
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
        catch {
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

      for (o <- offsetRanges) {
        logger.info(s"Commit Offsets ---------------------- ${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
      // Commit the kafka offsets in kafka after successful processing of every batch
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
