package com.anuj.edh.oct.config

import org.apache.kafka.common.serialization.StringDeserializer
import net.ceedubs.ficus.Ficus._
import com.typesafe.config.{Config, ConfigFactory}

/**
 * KafkaConfig trait which will populate all the configurations from
 * spark-streaming.conf file from classpath.
 *
 * @author Anuj Singhal
 */
trait KafkaConfig extends ApplicationConfig {
  private lazy val kafkaConsumerConfig = config.as[Option[Config]]("kafka.consumer").getOrElse(ConfigFactory.empty())
  val kafkaParams1: Map[String, Object] = kafkaConsumerConfig.as[Option[Map[String, String]]]("kafkaParams").getOrElse(Map[String, Object]())
  val kafkaParams2 = Map[String, Object](
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  val kafkaParams = kafkaParams1 ++ kafkaParams2
  lazy val kafkaArrayTopics: Array[String] = Array(kafkaConsumerConfig.as[Option[String]]("topic").getOrElse(""));
  //val kafkaTopic: String = kafkaConsumerConfig.as[Option[String]]("topic").getOrElse("");
}
object KafkaConfig extends KafkaConfig
