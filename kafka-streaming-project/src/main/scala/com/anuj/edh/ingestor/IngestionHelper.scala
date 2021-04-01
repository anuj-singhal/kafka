package com.anuj.edh.ingestor

import java.io.IOException

import com.anuj.edh.oct.scalaxb.FlightPax
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.{AnnotationIntrospector, DeserializationFeature, JsonMappingException, ObjectMapper}
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
/**
  * 
  */
object IngestionHelper {

  val logger = LoggerFactory.getLogger(getClass)

  val xmlMapper: ObjectMapper = new XmlMapper().setSerializationInclusion(Include.NON_NULL).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  val introspector: AnnotationIntrospector = new JaxbAnnotationIntrospector(xmlMapper.getTypeFactory())

  def parseXML(message: String): FlightPax = {
    try {
      xmlMapper.setAnnotationIntrospector(introspector)
      xmlMapper.readValue(message, classOf[FlightPax])
    } catch {
      case e: Throwable => logger.error(s"Unknown Exception ${e.getMessage}"); null
    }
  }

  def getMessageFromXML(message: String) :FlightPax = {
    val messageXML = scala.xml.XML.loadString(message)
    val flightPaxMessage = scalaxb.fromXML[FlightPax](messageXML)
    logger.info("Flight Pax message processed successfully with XSD.")
    return flightPaxMessage
  }

}
