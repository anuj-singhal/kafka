package com.anuj.edh.ingestor

import java.text.SimpleDateFormat
import java.util.Calendar

import com.anuj.edh.oct.utils.CommonUtils
import kafka.utils.ZkUtils

import scala.collection.JavaConverters._

object ZookeeperTest extends App{
 /* val zkUtils = ZkUtils.apply("127.0.0.1:2181", 10000, 10000, false)
  val offset = "flight-pax:0:1234,flight-pax:1:1233"
  val (offsetsRangesStrOpt, _) = zkUtils.readDataMaybeNull("/my_node/kafka")
  println("Paths --------- " + offsetsRangesStrOpt.get)
  zkUtils.updatePersistentPath("/my_node/kafka",offset)
  val (offsetsRangesStrOpt2, _) = zkUtils.readDataMaybeNull("/my_node/kafka")
  println("Paths --------- " + offsetsRangesStrOpt2.get)*/
 /*val l= List(1L,2L,3L,4L)
  l.map(java.lang.Long.valueOf).asJava
  println(l)
  val mm = Map(
    "a" -> 1L,
    "b" -> 2L
  )
  val m = mm.map(x => (x._1, java.lang.Long.valueOf(x._2))).asJava*/
 /* val currentTime = Calendar.getInstance().getTime()
 val sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
  //val date = sdf.parse(currentTime);
  println(currentTime);
  val cc = sdf.format(currentTime);*/
 val t = CommonUtils().getTimestamp("yyyy-MM-dd HH:mm:ss:SSS")
 println(t.toString)
}
