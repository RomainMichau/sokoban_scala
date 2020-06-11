package com.rmichau.soko.Console
import scala.io.StdIn
import scala.swing._
import scala.swing.event._

object ConsoleHelper {
  def waitForInput:Int =    System.console().reader().read().toChar
}
