package com.anuj.edh.oct.entity

case class FlightPaxDetails(
                             flightdate  : Option[String] = None,
                             carriercode  : Option[String] = None,
                             flightnumber  : Option[String] = None,
                             flightsuffix  : Option[String] = None,
                             departureairport  : Option[String] = None,
                             arrivalairport  : Option[String] = None,
                             departurenumber  : Option[Int] = None,
                             legnumber  : Option[Int] = None,
                             var forecastload_seatclasses  : Option[String] = None,
                             var forecastload_loadcount  : Option[Int] = None,
                             var bookedload_seatclasses  : Option[String] = None,
                             var bookedload_loadcount  : Option[Int]  = None,
                             var checkedload_seatclasses  :  Option[String] = None,
                             var checkedload_loadcount  : Option[Int] = None,
                             var boardedload_seatclasses  :  Option[String] = None,
                             var boardedLoad_loadCount  : Option[Int] = None,
                             actiontype  :  Option[String] = None,
                             flightpaxtimestamp  : Option[String] = None,
                             created_time  : Option[java.sql.Timestamp] = None,
                             day  : Option[Int] = None
                           )

case class FlightPaxConnectionDetails(
                                       flightdate  : Option[String] = None,
                                       carriercode : Option[String] = None,
                                       flightnumber  : Option[String] = None,
                                       flightsuffix  : Option[String] = None,
                                       departureairport  : Option[String] = None,
                                       arrivalairport  : Option[String] = None,
                                       departurenumber : Option[Int] = None,
                                       legnumber : Option[Int] = None,
                                       var connectiondirection : Option[String] = None,
                                       var connectionflightdate  : Option[String] = None,
                                       var connectioncarriercode : Option[String] = None,
                                       var connectionflightnumber  : Option[String] = None,
                                       var connectionflightsuffix  : Option[String] = None,
                                       var connectiondepartureairport  : Option[String] = None,
                                       var connectionarrivalairport  : Option[String] = None,
                                       var connectiondeparturenumber : Option[Int] = None,
                                       var connectionlegnumber : Option[Int] = None,
                                       var stationdatetime : Option[String] = None,
                                       var forecastload_seatclasses  : Option[String] = None,
                                       var forecastload_loadcount  : Option[Int] = None,
                                       var bookedload_seatclasses  : Option[String] = None,
                                       var bookedload_loadcount  : Option[Int] = None,
                                       var checkedload_seatclasses : Option[String] = None,
                                       var checkedload_loadcount : Option[Int] = None,
                                       var boardedload_seatclasses : Option[String] = None,
                                       var boardedload_loadcount : Option[Int] = None,
                                       var baggageseatclass  : Option[String] = None,
                                       var bagcount  : Option[Int] = None,
                                       actiontype  : Option[String] = None,
                                       flightpaxtimestamp  : Option[String] = None,
                                       created_time  : Option[java.sql.Timestamp] = None,
                                       day : Option[Int] = None
                                     )
/*case class FlightPaxDetails(
                             created_time  : Option[String] = None,
                             actiontype  : Option[String] = None,
                             flightPaxtimestamp  : Option[String] = None,
                             arrivalAirport  : Option[String] = None,
                             carrierCode  : Option[String] = None,
                             departureAirport  : Option[String] = None,
                             flightDate  : Option[String] = None,
                             flightNumber  : Option[String] = None,
                             departureNumber  : Option[Int] = None,
                             flightSuffix  : Option[String] = None,
                             legNumber  : Option[Int] = None,
                             var forecastLoad_SeatClasses  : Option[String]  = None,
                             var forecastLoad_loadCount  :  Option[Int] = None,
                             var bookedLoad_SeatClasses  : Option[String] = None,
                             var bookedLoad_loadCount  :  Option[Int] = None,
                             var checkedLoad_SeatClasses  : Option[String] = None,
                             var checkedLoad_loadCount  :  Option[Int] = None,
                             var boardedLoad_SeatClasses  : Option[String] = None,
                             var boardedLoad_loadCount  : Option[Int] = None,
                             day  : Option[Int] = None
                        )*/
                        
/*case class FlightPaxConnectionDetails(
                             flightDate	: Option[String] = None,
                             carrierCode	: Option[String] = None,
                             flightNumber	: Option[String] = None,
                             flightSuffix	: Option[String] = None,
                             departureAirport	: Option[String] = None,
                             arrivalAirport	: Option[String] = None,
                             departureNumber	: Option[Int] = None,
                             legNumber	: Option[Int] = None,
                             var connectionDirection	: Option[String] = None,
                             var connectionflightDate	: Option[String] = None,
                             var connectioncarrierCode	: Option[String] = None,
                             var connectionflightNumber	: Option[String] = None,
                             var connectionflightSuffix	: Option[String] = None,
                             var connectiondepartureAirport	: Option[String] = None,
                             var connectionarrivalAirport	: Option[String] = None,
                             var connectiondepartureNumber	: Option[Int] = None,
                             var connectionlegNumber	: Option[Int] = None,
                             var stationDateTime	: Option[String] = None,
                             var forecastLoad_SeatClasses	: Option[String] = None,
                             var forecastLoad_loadCount	: Option[Int] = None,
                             var bookedLoad_SeatClasses	: Option[String] = None,
                             var bookedLoad_loadCount	: Option[Int] = None,
                             var checkedLoad_SeatClasses	: Option[String] = None,
                             var checkedLoad_loadCount	: Option[Int] = None,
                             var boardedLoad_SeatClasses	: Option[String] = None,
                             var boardedLoad_loadCount	: Option[Int] = None,
                             var baggageseatClass	: Option[String] = None,
                             var bagCount	: Option[Int] = None,
                             Actiontype	: Option[String] = None,
                             flightPaxtimestamp	: Option[String] = None,
                             created_time	: Option[String] = None,
                             day	: Option[Int] = None
                        )*/
/*case class BoardedLoadCount(
                           loadCount : Int
                      )
case class BoardedLoadSeat(
                             seatClass : String
                           )*/
case class GenericMessage(
                      id : String,
                      xmlMessage: String,
                      status: String,
                      exception: Option[String] = None
               //flightPaxConnection : FlightPaxConnectionDetails
    )