package com.anuj.edh.oct.transform

import java.util.Calendar

import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails}
import com.anuj.edh.oct.scalaxb.FlightPax
import com.anuj.edh.oct.utils.CommonUtils
import com.anuj.edh.oct.entity.{FlightPaxConnectionDetails, FlightPaxDetails}
import com.anuj.edh.oct.scalaxb.FlightPax
import com.anuj.edh.oct.utils.CommonUtils

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

class FlightPaxDriver() {
  def processFlightPaxDetails(flightPax: FlightPax): Seq[FlightPaxDetails] = {
    //var flightPaxDetails: FlightPaxDetails = null;
    val flightPaxDetailsList = new ListBuffer[FlightPaxDetails]
    var flightPaxDetails = FlightPaxDetails(
      created_time = Some(CommonUtils().getTimestamp("yyyy-MM-dd HH:mm:ss:SSS")),
      actiontype = if (flightPax.actionType.isDefined) Some(flightPax.actionType.get.toString) else None,
      flightpaxtimestamp  = Some(flightPax.timestamp.toString),
      arrivalairport  = if (flightPax.flightId.arrivalAirport.isDefined) flightPax.flightId.arrivalAirport else None,
      carriercode  = Some(flightPax.flightId.carrierCode),
      departureairport = Some(flightPax.flightId.departureAirport),
      flightdate = Some(flightPax.flightId.flightDate.toString),
      flightnumber = Some(flightPax.flightId.flightNumber),
      departurenumber = if (flightPax.flightId.departureNumber.isDefined) flightPax.flightId.departureNumber else None,
      flightsuffix = if (flightPax.flightId.flightSuffix.isDefined) flightPax.flightId.flightSuffix else None,
      legnumber = if (flightPax.flightId.legNumber.isDefined) flightPax.flightId.legNumber else None,
      day = Some(CommonUtils().getDaysDifference("1980-01-01", flightPax.flightId.flightDate.toString))
    )
    if (flightPax.pax.isDefined) {
      val boardedLoadMap: Map[String, Int] = Map();
      val forecastLoadMap: Map[String, Int] = Map();
      val bookedLoadMap: Map[String, Int] = Map();
      val checkedLoadMap: Map[String, Int] = Map();
      if (flightPax.pax.get.boardedLoad.isDefined) {
        flightPax.pax.get.boardedLoad.get.loadCount.toList.foreach(load => {
          boardedLoadMap += (load.seatClass.toString -> load.value)
        })
      }
      if (flightPax.pax.get.forecastLoad.isDefined) {
        flightPax.pax.get.forecastLoad.get.loadCount.toList.foreach(load => {
          forecastLoadMap += (load.seatClass.toString -> load.value)
        })
      }
      if (flightPax.pax.get.bookedLoad.isDefined) {
        flightPax.pax.get.bookedLoad.get.loadCount.toList.foreach(load => {
          bookedLoadMap += (load.seatClass.toString -> load.value)
        })
      }
      if (flightPax.pax.get.checkedLoad.isDefined) {
        flightPax.pax.get.checkedLoad.get.loadCount.toList.foreach(load => {
          checkedLoadMap += (load.seatClass.toString -> load.value)
        })
      }
      if (boardedLoadMap.size > 0 || forecastLoadMap.size > 0 || bookedLoadMap.size > 0 || checkedLoadMap.size > 0) {
        if (boardedLoadMap.size > 0) {
          boardedLoadMap.foreach { case (seatClass, loadCount) => {
            //val seatClass : Option[String] = seatClass
            var fpDetails: FlightPaxDetails = flightPaxDetails.copy()
            fpDetails.boardedLoad_loadCount = Some(loadCount)
            fpDetails.boardedload_seatclasses = Some(seatClass)
            if (forecastLoadMap.size > 0 && forecastLoadMap.contains(seatClass)) {
              fpDetails.forecastload_loadcount = forecastLoadMap.get(seatClass)
              fpDetails.forecastload_seatclasses = Some(seatClass)
            }
            if (bookedLoadMap.size > 0 && bookedLoadMap.contains(seatClass)) {
              fpDetails.bookedload_loadcount = bookedLoadMap.get(seatClass)
              fpDetails.bookedload_seatclasses = Some(seatClass)
            }
            if (checkedLoadMap.size > 0 && checkedLoadMap.contains(seatClass)) {
              fpDetails.checkedload_loadcount = checkedLoadMap.get(seatClass)
              fpDetails.checkedload_seatclasses = Some(seatClass)
            }
            flightPaxDetailsList.append(fpDetails)
          }
          }
        } else if (forecastLoadMap.size > 0) {
          forecastLoadMap.foreach { case (seatClass, loadCount) => {
            //val seatClass: Option[String] = seatClass
            var fpDetails: FlightPaxDetails = flightPaxDetails.copy()
            flightPaxDetails.forecastload_loadcount = Some(loadCount)
            flightPaxDetails.forecastload_seatclasses = Some(seatClass)
            if (bookedLoadMap.size > 0 && bookedLoadMap.contains(seatClass)) {
              flightPaxDetails.bookedload_loadcount = bookedLoadMap.get(seatClass)
              flightPaxDetails.bookedload_seatclasses = Some(seatClass)
            }
            if (checkedLoadMap.size > 0 && checkedLoadMap.contains(seatClass)) {
              flightPaxDetails.checkedload_loadcount = checkedLoadMap.get(seatClass)
              flightPaxDetails.checkedload_seatclasses = Some(seatClass)
            }
            flightPaxDetailsList.append(fpDetails)
          }
          }
        } else if (bookedLoadMap.size > 0) {
          bookedLoadMap.foreach { case (seatClass, loadCount) => {
            //val seatClass: Option[String] = seatClass
            var fpDetails: FlightPaxDetails = flightPaxDetails.copy()
            flightPaxDetails.bookedload_loadcount = Some(loadCount)
            flightPaxDetails.bookedload_seatclasses = Some(seatClass)
            if (checkedLoadMap.size > 0 && checkedLoadMap.contains(seatClass)) {
              flightPaxDetails.checkedload_loadcount = checkedLoadMap.get(seatClass)
              flightPaxDetails.checkedload_seatclasses = Some(seatClass)
            }
            flightPaxDetailsList.append(fpDetails)
          }
          }
        } else if (checkedLoadMap.size > 0) {
          checkedLoadMap.foreach { case (seatClass, loadCount) => {
            //val seatClass:Option[String] = seatClass
            var fpDetails: FlightPaxDetails = flightPaxDetails.copy()
            flightPaxDetails.checkedload_loadcount = Some(loadCount)
            flightPaxDetails.checkedload_seatclasses = Some(seatClass)
            flightPaxDetailsList.append(fpDetails)
          }
          }
        }
      }
    }
    else
      flightPaxDetailsList.append(flightPaxDetails)
    /*if (flightPaxDetailsList.size == 0)
      flightPaxDetailsList.append(flightPaxDetails)*/
    return flightPaxDetailsList.toSeq
  }

  def processFlightPaxConnectionDetails(flightPax: FlightPax): Seq[FlightPaxConnectionDetails] = {
    //var flightPaxDetails: FlightPaxDetails = null;
    val flightPaxConnectionDetailsList = new ListBuffer[FlightPaxConnectionDetails]
    var flightPaxConnectionDetails = FlightPaxConnectionDetails(
      created_time = Some(CommonUtils().getTimestamp("yyyy-MM-dd HH:mm:ss:SSS")),
      actiontype = if (flightPax.actionType.isDefined) Some(flightPax.actionType.get.toString) else None,
      flightpaxtimestamp  = Some(flightPax.timestamp.toString),
      arrivalairport = if(flightPax.flightId.arrivalAirport.isDefined) flightPax.flightId.arrivalAirport else None,
      carriercode  = Some(flightPax.flightId.carrierCode),
      departureairport  = Some(flightPax.flightId.departureAirport),
      flightdate  = Some(flightPax.flightId.flightDate.toString),
      flightnumber  = Some(flightPax.flightId.flightNumber),
      departurenumber  = if(flightPax.flightId.departureNumber.isDefined) flightPax.flightId.departureNumber else None,
      flightsuffix  = if(flightPax.flightId.flightSuffix.isDefined) flightPax.flightId.flightSuffix else None,
      legnumber = if(flightPax.flightId.legNumber.isDefined) flightPax.flightId.legNumber else None,
      day = Some(CommonUtils().getDaysDifference("1980-01-01", flightPax.flightId.flightDate.toString))
    )
    if (flightPax.connections.isDefined) {
      flightPax.connections.get.connectionFlight.foreach(cFlight => {
        var fpcDetails: FlightPaxConnectionDetails = flightPaxConnectionDetails.copy()
        fpcDetails.connectiondirection = Some(cFlight.connectionDirection.toString)
        fpcDetails.connectionflightdate = Some(cFlight.flightId.flightDate.toString)
        fpcDetails.connectioncarriercode = Some(cFlight.flightId.carrierCode.toString)
        fpcDetails.connectionflightnumber = Some(cFlight.flightId.flightNumber.toString)
        fpcDetails.connectionflightsuffix = if(cFlight.flightId.flightSuffix.isDefined) cFlight.flightId.flightSuffix else None
        fpcDetails.connectiondepartureairport = Some(cFlight.flightId.departureAirport.toString)
        fpcDetails.connectionarrivalairport = if(cFlight.flightId.arrivalAirport.isDefined) cFlight.flightId.arrivalAirport else None
        fpcDetails.connectiondeparturenumber = if(cFlight.flightId.departureNumber.isDefined) cFlight.flightId.departureNumber else None
        fpcDetails.connectionlegnumber = if(cFlight.flightId.legNumber.isDefined) cFlight.flightId.legNumber else None
        fpcDetails.stationdatetime = if(cFlight.stationDateTime.isDefined) Some(cFlight.stationDateTime.get.toString) else None
        val boardedLoadMap: Map[String, Int] = Map();
        val forecastLoadMap: Map[String, Int] = Map();
        val bookedLoadMap: Map[String, Int] = Map();
        val checkedLoadMap: Map[String, Int] = Map();
        val baggageMap: Map[String, Int] = Map();
        //if (cFlight.baggage.isDefined) {
          /*cFlight.baggage.get.bagCount.toList.foreach(load => {
            baggageMap += (load.seatClass.toString -> load.value)
          })*/
        //}
        if (cFlight.pax.isDefined || cFlight.baggage.isDefined) {
          if (cFlight.baggage.isDefined) {
            cFlight.baggage.get.bagCount.toList.foreach(load => {
              baggageMap += (load.seatClass.toString -> load.value)
            })
          }
          if (cFlight.pax.get.boardedLoad.isDefined) {
            cFlight.pax.get.boardedLoad.get.loadCount.toList.foreach(load => {
              boardedLoadMap += (load.seatClass.toString -> load.value)
            })
          }
          if (cFlight.pax.get.forecastLoad.isDefined) {
            cFlight.pax.get.forecastLoad.get.loadCount.toList.foreach(load => {
              forecastLoadMap += (load.seatClass.toString -> load.value)
            })
          }
          if (cFlight.pax.get.bookedLoad.isDefined) {
            cFlight.pax.get.bookedLoad.get.loadCount.toList.foreach(load => {
              bookedLoadMap += (load.seatClass.toString -> load.value)
            })
          }
          if (cFlight.pax.get.checkedLoad.isDefined) {
            cFlight.pax.get.checkedLoad.get.loadCount.toList.foreach(load => {
              checkedLoadMap += (load.seatClass.toString -> load.value)
            })
          }
        }
        else
          flightPaxConnectionDetailsList.append(fpcDetails)

        if (baggageMap.size > 0 || boardedLoadMap.size > 0 || forecastLoadMap.size > 0 || bookedLoadMap.size > 0 || checkedLoadMap.size > 0) {
          if (boardedLoadMap.size > 0) {
            boardedLoadMap.foreach { case (seatClass, loadCount) => {
              //val seatClass : Option[String] = seatClass
              var fpcPaxDetails: FlightPaxConnectionDetails = fpcDetails.copy()
              fpcPaxDetails.boardedload_loadcount = Some(loadCount)
              fpcPaxDetails.boardedload_seatclasses = Some(seatClass)
              if (forecastLoadMap.size > 0 && forecastLoadMap.contains(seatClass)) {
                fpcPaxDetails.forecastload_loadcount = forecastLoadMap.get(seatClass)
                fpcPaxDetails.forecastload_seatclasses = Some(seatClass)
              }
              if (bookedLoadMap.size > 0 && bookedLoadMap.contains(seatClass)) {
                fpcPaxDetails.bookedload_loadcount = bookedLoadMap.get(seatClass)
                fpcPaxDetails.bookedload_seatclasses = Some(seatClass)
              }
              if (checkedLoadMap.size > 0 && checkedLoadMap.contains(seatClass)) {
                fpcPaxDetails.checkedload_loadcount = checkedLoadMap.get(seatClass)
                fpcPaxDetails.checkedload_seatclasses = Some(seatClass)
              }
              if (baggageMap.size > 0 && baggageMap.contains(seatClass)) {
                fpcPaxDetails.bagcount = baggageMap.get(seatClass)
                fpcPaxDetails.baggageseatclass = Some(seatClass)
              }
              flightPaxConnectionDetailsList.append(fpcPaxDetails)
            }
            }
          } else if (forecastLoadMap.size > 0) {
            forecastLoadMap.foreach { case (seatClass, loadCount) => {
              //val seatClass: Option[String] = seatClass
              var fpcPaxDetails: FlightPaxConnectionDetails = fpcDetails.copy()
              fpcPaxDetails.forecastload_loadcount = Some(loadCount)
              fpcPaxDetails.forecastload_seatclasses = Some(seatClass)
              if (bookedLoadMap.size > 0 && bookedLoadMap.contains(seatClass)) {
                fpcPaxDetails.bookedload_loadcount = bookedLoadMap.get(seatClass)
                fpcPaxDetails.bookedload_seatclasses = Some(seatClass)
              }
              if (checkedLoadMap.size > 0 && checkedLoadMap.contains(seatClass)) {
                fpcPaxDetails.checkedload_loadcount = checkedLoadMap.get(seatClass)
                fpcPaxDetails.checkedload_seatclasses = Some(seatClass)
              }
              if (baggageMap.size > 0 && baggageMap.contains(seatClass)) {
                fpcPaxDetails.bagcount = baggageMap.get(seatClass)
                fpcPaxDetails.baggageseatclass = Some(seatClass)
              }
              flightPaxConnectionDetailsList.append(fpcPaxDetails)
            }
            }
          } else if (bookedLoadMap.size > 0) {
            bookedLoadMap.foreach { case (seatClass, loadCount) => {
              //val seatClass: Option[String] = seatClass
              var fpcPaxDetails: FlightPaxConnectionDetails = fpcDetails.copy()
              fpcPaxDetails.bookedload_loadcount = Some(loadCount)
              fpcPaxDetails.bookedload_seatclasses = Some(seatClass)
              if (checkedLoadMap.size > 0 && checkedLoadMap.contains(seatClass)) {
                fpcPaxDetails.checkedload_loadcount = checkedLoadMap.get(seatClass)
                fpcPaxDetails.checkedload_seatclasses = Some(seatClass)
              }
              if (baggageMap.size > 0 && baggageMap.contains(seatClass)) {
                fpcPaxDetails.bagcount = baggageMap.get(seatClass)
                fpcPaxDetails.baggageseatclass = Some(seatClass)
              }
              flightPaxConnectionDetailsList.append(fpcPaxDetails)
            }
            }
          } else if (checkedLoadMap.size > 0) {
            checkedLoadMap.foreach { case (seatClass, loadCount) => {
              //val seatClass:Option[String] = seatClass
              var fpcPaxDetails: FlightPaxConnectionDetails = fpcDetails.copy()
              fpcPaxDetails.checkedload_loadcount = Some(loadCount)
              fpcPaxDetails.checkedload_seatclasses = Some(seatClass)
              if (baggageMap.size > 0 && baggageMap.contains(seatClass)) {
                fpcPaxDetails.bagcount = baggageMap.get(seatClass)
                fpcPaxDetails.baggageseatclass = Some(seatClass)
              }
              flightPaxConnectionDetailsList.append(fpcPaxDetails)
            }
            }
          } else if (baggageMap.size > 0) {
            baggageMap.foreach { case (seatClass, loadCount) => {
              //val seatClass:Option[String] = seatClass
              var fpcPaxDetails: FlightPaxConnectionDetails = fpcDetails.copy()
              fpcPaxDetails.checkedload_loadcount = Some(loadCount)
              fpcPaxDetails.checkedload_seatclasses = Some(seatClass)
              flightPaxConnectionDetailsList.append(fpcPaxDetails)
            }
            }
          }
        }
      })
    }
    else
      flightPaxConnectionDetailsList.append(flightPaxConnectionDetails)
    return flightPaxConnectionDetailsList.toSeq
  }
}
object FlightPaxDriver {
  def apply(): FlightPaxDriver = new FlightPaxDriver()
}