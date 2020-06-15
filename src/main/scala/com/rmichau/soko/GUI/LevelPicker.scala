package com.rmichau.soko.GUI


import java.io.File
import java.net.URI

import javafx.event.EventHandler
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import javafx.event.ActionEvent
import javafx.stage.Stage
import scalafx.scene.AccessibleRole.{Button, ComboBox}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, Label}
import scalafx.scene.layout.GridPane

class LevelPicker {

    def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  val dialogStage = new Stage()
  var res: URI = null
  val files: ObservableBuffer[File]= ObservableBuffer(this.recursiveListFiles(new File(SokoStage.levelPath.replace("file:","")))
    .filter(_.isFile).toList)

  def pickLevel = {
    val grid = new GridPane()
    val comboBox = new ComboBox[File]() {
      items = files
    }
    val button = new Button("ok")
    button.onAction =new EventHandler[ActionEvent]{
      def handle(actionEvent: ActionEvent) = {
        dialogStage.close()
        res = comboBox.value.value.toURI
      }
    }
    grid.add(new Label("choose a level"),0,0 )
    grid.add(comboBox,0,1 )
    grid.add(button,0,2)

    val scene = new Scene{
      content = grid
    }
    dialogStage.setScene(scene)
    dialogStage.showAndWait()
    res
  }

}
