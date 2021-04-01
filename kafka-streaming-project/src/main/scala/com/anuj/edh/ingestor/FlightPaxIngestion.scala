package com.anuj.edh.ingestor

import com.anuj.edh.oct.config.{KafkaConfig, SparkAppConfig}
import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails, GenericMessage}
import com.anuj.edh.oct.transform.FlightPaxDriver
import com.anuj.edh.oct.utils.CommonUtils
import com.anuj.edh.oct.config.{KafkaConfig, SparkAppConfig}
import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails, GenericMessage}
import com.anuj.edh.oct.transform.FlightPaxDriver
import com.anuj.edh.oct.utils.CommonUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.slf4j.LoggerFactory

object FlightPaxIngestion extends SparkAppConfig{
  val logger = LoggerFactory.getLogger(getClass)
  def main(args: Array[String]): Unit = {
    // parameters passing from commandline
    val hdfsPath = args(0);
    val hiveDB = args(1);
    val flightPaxHiveTable = args(2);
    val flightConnHiveTable = args(3);
    var offsetRanges = Array.empty[OffsetRange]
    // create a direct stream using spark and kafka configurations
    val stream = KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String]
      (KafkaConfig.kafkaArrayTopics, KafkaConfig.kafkaParams))
    stream.transform { rdd =>
      // get the current offsets from kafka producer
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }.foreachRDD { rddRecord =>
      for (o <- offsetRanges) {
        logger.info(s"Processing Offsets ---------------------- ${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
      // convert consumer record into RDD[String]
      val rdd = rddRecord.map(x => x.value()).filter(_ != null)
      val batchCount = rdd.count()
      logger.info(s"Total messages received in a batch: $batchCount")
      // only process when there is data in RDD batch
      if (!rdd.isEmpty()) {
        val timer = System.currentTimeMillis() + " ms"
        val batchCount = rdd.count()
        logger.info(s"PROCESSING STARTED AT TIME : $timer INPUT RDD COUNT : $batchCount")
        val messageRdd = rdd.map(eachMessage => {
          try {
            // parse the xml message based on XSD and case class
            val flightMessage = IngestionHelper.getMessageFromXML(eachMessage)
            // process flightpax
            val flightPax = FlightPaxDriver().processFlightPaxDetails(flightMessage)
            // process flightpax connection
            val flightPaxConnection = FlightPaxDriver().processFlightPaxConnectionDetails(flightMessage)
            // process original message
            val genericMessage = CommonUtils().getOriginalMessage(eachMessage, "Success")
            logger.info("Message Successfully processed...")
            (genericMessage, flightPax, flightPaxConnection)
          }
          catch {
            case e: Exception => {
              val flightPax = Seq(FlightPaxDetails())
              val flightPaxConnectionDetails = Seq(FlightPaxConnectionDetails())
              // capture any failure
              val genericMessage = GenericMessage(eachMessage, "Failed", e.getMessage)
              logger.info("Message Failed...")
              (genericMessage, flightPax, flightPaxConnectionDetails)
            }
          }
        })
        import spark.implicits._
        try {
          val path = hdfsPath + CommonUtils().getCurrentDateWithHour
          logger.info(path)
          // Store generic message into parquet in HDFS
          val genericMessageDF = messageRdd.map(x => x._1).toDF()
          genericMessageDF.coalesce(1).write.format("parquet").mode("append").save(path)
          // Strore flightpax into hive table
          val flightPaxDF = messageRdd.map(x => x._2).flatMap(x => x).toDF()
          flightPaxDF.coalesce(1).write.format("Hive").mode(SaveMode.Append).insertInto(hiveDB + "." + flightPaxHiveTable)
          // Strore flightpax connection into hive table
          val flightPaxConnectionDF = messageRdd.map(x => x._3).flatMap(x => x).toDF()
          flightPaxConnectionDF.coalesce(1).write.format("Hive").mode(SaveMode.Append).insertInto(hiveDB + "." + flightConnHiveTable);
        }
        catch {
          case e: Throwable => {
            logger.error("Undefined exception: - " + e.printStackTrace())
          }
        }
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
