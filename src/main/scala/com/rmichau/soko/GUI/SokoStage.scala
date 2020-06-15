package com.rmichau.soko.GUI

import javafx.stage.Stage
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image

object SokoStage {
  private val ressourcePath = getClass.getResource("/img/Box.jpg").toString
  val stage = new PrimaryStage(){
    title = "sokoSolve"
    icons.add(new Image(ressourcePath))
  }
  def changeScene(scene: Scene) = stage.scene = scene
  val imgPath = getClass.getResource("/img/").toString
  val levelPath = getClass.getResource("/levels/").toString


}
