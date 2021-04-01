package com.anuj.edh.oct.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.anuj.edh.oct.entity.GenericMessage
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import java.sql.Timestamp

import com.anuj.edh.oct.entity.GenericMessage

import scala.io.Source

class CommonUtils {
  val logger = LoggerFactory.getLogger(getClass)
  /**
    *
    * @param xmlFileName String : File Name available in resource folder to be considered
    * @return String : Returns the First line of the file
    */
  def getXmlSource(xmlFileName: String): String = {
    var xmlFileWithPath: String =  xmlFileName
    var filePath = Source.fromFile("C:/Work/Ethiad/eclipse/ws_xspark/spark-framework/src/main/resources/FlightPax.xml")
    var srcData = filePath.getLines().mkString
    return srcData
  }
  def getOriginalMessage(message: String, status:String): GenericMessage ={
    val timer = System.currentTimeMillis().toString
    val genericMessage = GenericMessage(timer, message,status)
    return genericMessage
  }
  def getDaysDifference(startDate: String, endDate: String): Int ={
    //val startDate = "1980-01-01"
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val oldDate = LocalDate.parse(startDate, formatter)
    //val currentDate = "2015-02-25"
    val newDate = LocalDate.parse(endDate, formatter)
    val daysDiff = newDate.toEpochDay() - oldDate.toEpochDay()
    return daysDiff.toInt
  }
  def getCurrentDateWithHour(): String = {
    val dateFormatter = new SimpleDateFormat("yyyy-MM-dd-hh")
    var currentDateConvert = new Date()
    val currentDateWithHour = dateFormatter.format(currentDateConvert)
    return currentDateWithHour
  }
  def getCurrentConvertedDate(pattern: String): String = {
    val currentTime = Calendar.getInstance().getTime()
    val sdf =  new SimpleDateFormat(pattern);
    return sdf.format(currentTime);
  }

  def getTimestamp(pattern: String) : Timestamp = {
    val currentTime = getCurrentConvertedDate(pattern)
    val sdf =  new SimpleDateFormat(pattern);
    val currentFormattedDate = sdf.parse(currentTime);
    val currentTimeStamp = new Timestamp(currentFormattedDate.getTime());
    return currentTimeStamp
  }

}

object CommonUtils {
  def apply(): CommonUtils = new CommonUtils()
}