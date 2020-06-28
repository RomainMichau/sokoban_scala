package com.rmichau.soko.GUI


import java.io.File
import java.net.URI

import com.rmichau.soko.util.Util
import javafx.event.{ActionEvent, EventHandler}
import javafx.stage.Stage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, Label}
import scalafx.scene.layout.GridPane

object LevelPicker {


  val dialogStage = new Stage()
  var res: URI = new URI("")
  val files: ObservableBuffer[File]= ObservableBuffer(Util.recursiveListFiles(new File("levels"))
    .filter(_.isFile).toList)

  def pickLevel: URI = {
    val grid = new GridPane()
    val comboBox = new ComboBox[File]() {
      items = files
    }
    val button = new Button("ok")
    button.onAction = (actionEvent: ActionEvent) => {
      dialogStage.close()
      res = comboBox.value.value.toURI
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
