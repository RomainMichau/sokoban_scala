package com.rmichau.soko.GUI

import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image

import scala.io.Source

object SokoStage {
  Source.fromResource("movies.txt")
  private val ressourcePath = getClass.getResource("/img/Box.jpg").toString
  val stage: PrimaryStage = new PrimaryStage() {
    title = "sokoSolve"
    icons.add(new Image(ressourcePath))
  }
  def setScene(scene: Scene): Unit = stage.scene = scene
  val imgPath: String = getClass.getResource("/img/").toString

}
