// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package com.anuj.edh.oct.scalaxb

/**
			Copyright 2016 IBS Software Services (P) Ltd. All Rights Reserved.
			This document is the proprietary information of IBS Software Services (P) Ltd. Use is subject to license terms.		
		
*/


case class BagCount(value: Int,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val seatClass = attributes("@seatClass").as[SeatClasses]
}

      


case class Baggage(bagCount: Seq[BagCount] = Nil,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val totalBagCount = attributes.get("@totalBagCount") map { _.as[Int]}
}

      


case class ConnectionFlight(flightId: FlightId,
  stationDateTime: Option[javax.xml.datatype.XMLGregorianCalendar] = None,
  pax: Option[Pax] = None,
  baggage: Option[Baggage] = None,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val connectionDirection = attributes("@connectionDirection").as[ConnectionDirectionTypes]
}

      


case class Connections(connectionFlight: Seq[ConnectionFlight] = Nil)
      


case class FlightPax(flightId: FlightId,
  pax: Option[Pax] = None,
  connections: Option[Connections] = None,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val actionType = attributes.get("@actionType") map { _.as[ActionTypes]}
  lazy val messageReference = attributes.get("@messageReference") map { _.as[String]}
  lazy val batchReference = attributes.get("@batchReference") map { _.as[String]}
  lazy val sequenceNumber = attributes.get("@sequenceNumber") map { _.as[Int]}
  lazy val batchEndInd = attributes.get("@batchEndInd") map { _.as[Boolean]}
  lazy val timestamp = attributes("@timestamp").as[javax.xml.datatype.XMLGregorianCalendar]
  lazy val originator = attributes.get("@originator") map { _.as[String]}
  lazy val proprietaryNotice = attributes("@proprietaryNotice").as[String]
  lazy val company = attributes.get("@company") map { _.as[String]}
  lazy val tenant = attributes.get("@tenant") map { _.as[String]}
  lazy val schemaVersion = attributes("@schemaVersion").as[Float]
}

      

