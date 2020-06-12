package com.rmichau.soko.Console

object ConsoleHelper {
  def waitForInput:Int =    System.console().reader().read().toChar
}
