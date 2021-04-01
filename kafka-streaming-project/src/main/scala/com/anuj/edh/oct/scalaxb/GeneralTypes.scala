// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package com.anuj.edh.oct.scalaxb

/**
			Copyright 2018 IBS Software Private Limited. All Rights Reserved.
			This document is the proprietary information of IBS Software Private Limited. Use is subject to license terms.	
		
*/


case class GeneralTypes(attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val schemaVersion = attributes("@schemaVersion").as[Float]
}

      


/** Special duty codes
*/
case class SpecialDutyCodes(specialDutyCode: Seq[String] = Nil)
      

sealed trait CrewTypes

object CrewTypes {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[CrewTypes]): CrewTypes = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: CrewTypes) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[CrewTypes] = Seq(FValue, CValue)
}

case object FValue extends CrewTypes { override def toString = "F" }
case object CValue extends CrewTypes { override def toString = "C" }


/** Specification of weight with unit
*/
case class WeightType(value: Int,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val unit = attributes("@unit").as[WeightUnitTypes]
}

      

sealed trait UnitOfMeasures

object UnitOfMeasures {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[UnitOfMeasures]): UnitOfMeasures = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: UnitOfMeasures) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[UnitOfMeasures] = Seq(KILO, POUNDS)
}

case object KILO extends UnitOfMeasures { override def toString = "KILO" }
case object POUNDS extends UnitOfMeasures { override def toString = "POUNDS" }

sealed trait WeightUnitTypes

object WeightUnitTypes {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[WeightUnitTypes]): WeightUnitTypes = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: WeightUnitTypes) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[WeightUnitTypes] = Seq(KGValue2, LBSValue2)
}

case object KGValue2 extends WeightUnitTypes { override def toString = "KG" }
case object LBSValue2 extends WeightUnitTypes { override def toString = "LBS" }


/** Specification of length with unit
*/
case class LengthType(value: Int,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val unit = attributes("@unit").as[String]
}

      


/** Specification of volume with unit
*/
case class VolumeType(value: Int,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val unit = attributes("@unit").as[String]
}

      

sealed trait VolumeUnitTypes

object VolumeUnitTypes {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[VolumeUnitTypes]): VolumeUnitTypes = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: VolumeUnitTypes) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[VolumeUnitTypes] = Seq(Mu943, LValue3, GALValue)
}

case object Mu943 extends VolumeUnitTypes { override def toString = "M^3" }
case object LValue3 extends VolumeUnitTypes { override def toString = "L" }
case object GALValue extends VolumeUnitTypes { override def toString = "GAL" }


/** Fleet
*/
case class Fleet(value: String,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val fleetType = attributes.get("@fleetType") map { _.as[FleetTypes]}
}

      

sealed trait FleetTypes

object FleetTypes {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[FleetTypes]): FleetTypes = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: FleetTypes) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[FleetTypes] = Seq(AGREEMENT, BASE, LICENSE)
}

case object AGREEMENT extends FleetTypes { override def toString = "AGREEMENT" }
case object BASE extends FleetTypes { override def toString = "BASE" }
case object LICENSE extends FleetTypes { override def toString = "LICENSE" }


case class DaysOfWeek(dayOfWeek: Seq[Int] = Nil)
      

sealed trait PeriodTypes

object PeriodTypes {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[PeriodTypes]): PeriodTypes = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: PeriodTypes) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[PeriodTypes] = Seq(DValue2, WValue, MValue, YValue)
}

case object DValue2 extends PeriodTypes { override def toString = "D" }
case object WValue extends PeriodTypes { override def toString = "W" }
case object MValue extends PeriodTypes { override def toString = "M" }
case object YValue extends PeriodTypes { override def toString = "Y" }

sealed trait TimeModeTypes

object TimeModeTypes {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[TimeModeTypes]): TimeModeTypes = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: TimeModeTypes) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[TimeModeTypes] = Seq(UTC, LT)
}

case object UTC extends TimeModeTypes { override def toString = "UTC" }
case object LT extends TimeModeTypes { override def toString = "LT" }


case class SubParameter(attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val key = attributes("@key").as[String]
  lazy val valueAttribute = attributes("@value").as[String]
}

      


case class Parameter(subParameter: Seq[SubParameter] = Nil,
  attributes: Map[String, scalaxb.DataRecord[Any]] = Map.empty) {
  lazy val key = attributes("@key").as[String]
  lazy val valueAttribute = attributes("@value").as[String]
}

      


case class Parameters(parameter: Seq[Parameter] = Nil)
      


/** Duration or percentage
*/
case class DurationOrPercentage(durationorpercentageoption: scalaxb.DataRecord[Any])
      

trait DurationOrPercentageOption
sealed trait HaulTypes

object HaulTypes {
  def fromString(value: String, scope: scala.xml.NamespaceBinding)(implicit fmt: scalaxb.XMLFormat[HaulTypes]): HaulTypes = fmt.reads(scala.xml.Text(value), Nil) match {
    case Right(x: HaulTypes) => x
    case x => throw new RuntimeException(s"fromString returned unexpected value $x for input $value")
  }
  lazy val values: Seq[HaulTypes] = Seq(LValue4, SValue2)
}

case object LValue4 extends HaulTypes { override def toString = "L" }
case object SValue2 extends HaulTypes { override def toString = "S" }


case class CrewApplicability(crewType: Option[CrewTypes] = None,
  base: Option[String] = None,
  fleet: Option[String] = None,
  rank: Option[String] = None)
      


case class Fleets(fleet: Seq[String] = Nil)
      


case class Ranks(rank: Seq[String] = Nil)
      


case class Bases(base: Seq[String] = Nil)
      


/** Crew applicability
*/
case class CrewApplicabilityMulti(crewType: Option[CrewTypes] = None,
  fleets: Option[Fleets] = None,
  ranks: Option[Ranks] = None,
  bases: Option[Bases] = None)
      

case class FromToTimeGroupSequence(fromTime: javax.xml.datatype.XMLGregorianCalendar,
  toTime: javax.xml.datatype.XMLGregorianCalendar)

case class FromToDateTimeGroupSequence(fromDateTime: javax.xml.datatype.XMLGregorianCalendar,
  toDateTime: javax.xml.datatype.XMLGregorianCalendar)

case class StartEndDateTimeGroupSequence(startDateTime: javax.xml.datatype.XMLGregorianCalendar,
  endDateTime: javax.xml.datatype.XMLGregorianCalendar)

case class FromToDateGroupSequence(fromDate: javax.xml.datatype.XMLGregorianCalendar,
  toDate: javax.xml.datatype.XMLGregorianCalendar)
