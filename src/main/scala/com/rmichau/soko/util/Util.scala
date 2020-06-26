package com.rmichau.soko.util

import java.io.File

object Util {
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  case class Chrono() {
    val startingTime: Long = System.currentTimeMillis()
    def currentTime: Long = System.currentTimeMillis() - startingTime
  }
}
