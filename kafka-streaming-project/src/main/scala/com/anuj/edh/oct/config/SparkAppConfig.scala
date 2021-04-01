package com.anuj.edh.oct.config

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Duration

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import org.apache.spark.sql.SparkSession

import org.apache.spark.SparkContext

/**
 * SparkConfig trait which will populate all the configurations from
 * spark-streaming.conf file from classpath.
 *
 * @author Anuj Singhal
 */
trait SparkAppConfig  extends ApplicationConfig{

  private lazy val sparkConfig = config.getConfig("spark")

  private object SparkConf {
    lazy val params = sparkConfig.as[Option[Map[String, String]]]("sparkConf")
      .getOrElse( Map("spark.app.name" -> "FlightPax-Streaming",
        "spark.master" -> "yarn"
      )
      )
  }

  private object SparkStreamingConfig {
    lazy val batchMilliSeconds = sparkConfig.as[Option[Int]]("streamingContext.batchMilliSeconds").getOrElse(30000)
  }

  private lazy val sparkConf : SparkConf = {
    val sparkConf = new SparkConf()
    SparkConf.params.foreach {
      value => sparkConf.set(value._1, value._2)
    }
    sparkConf
  }
  
  implicit lazy val streamingContext : StreamingContext = new StreamingContext(sparkConf, Duration(SparkStreamingConfig.batchMilliSeconds))
  implicit lazy val spark = SparkSession.builder.config(streamingContext.sparkContext.getConf)
    .config("hive.exec.dynamic.partition", "true")
    .config("hive.exec.dynamic.partition.mode", "nonstrict").enableHiveSupport()
    .getOrCreate()
}
object SparkAppConfig extends SparkAppConfig
