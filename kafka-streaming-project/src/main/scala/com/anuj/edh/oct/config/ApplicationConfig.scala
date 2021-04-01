package com.anuj.edh.oct.config

import com.typesafe.config.ConfigFactory

trait ApplicationConfig {
  lazy val config = ConfigFactory.load()
}

object ApplicationConfig extends ApplicationConfig