package com.rmichau.soko.GUI

import com.rmichau.soko.Maze.SquareType.SquareType
import com.rmichau.soko.Maze._
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Tooltip
import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.GridPane

import scala.collection.immutable.HashMap

class SokoGui(maze: Maze) {

  val mazeProp: ObjectProperty[Maze] = new SimpleObjectProperty[Maze](maze)
  var gridNodes: Map[Coord, ImageView] = HashMap()
  private val fieldImages: Map[SquareType, Image] = {
    val imgPath = SokoStage.imgPath
    var mp: Map[SquareType, Image] = HashMap()
    SquareType.values.foreach { sq =>
      val url = imgPath + sq.toString + ".jpg"
      val img = getImageFromPath(url)
      if (img.width == null)
        throw new Exception(s"img $url does not exist")
      mp = mp + (sq -> img)
    }
    mp
  }
  private val marioImg = getImageFromPath(getClass.getResource("/img/mario.jpg").toString)
  val grid: GridPane = loadMaze()

  val keyEventManager: KeyEvent => Unit = { key: KeyEvent =>
    if (KeyCode.Up.getCode == key.getCode.getCode)
      this.movePlayer(UP)
    if (KeyCode.Down.getCode == key.getCode.getCode)
      this.movePlayer(DOWN)
    if (KeyCode.Left.getCode == key.getCode.getCode)
      this.movePlayer(LEFT)
    if (KeyCode.Right.getCode == key.getCode.getCode)
      this.movePlayer(RIGHT)
    if (KeyCode.Escape.getCode == key.getCode.getCode)
      maze.reinitGame()
    refreshGrid()
  }

  private def field = maze.getGameState.field

  private def posPlayer = maze.getGameState.playerPos

  def stage(): Unit = {

    val scene = new Scene {
      onKeyPressed = keyEventManager
      content = grid
    }
    SokoStage.setScene(scene)
  }

  private def refreshGrid(): Unit = {
    (0 until Maze.getNbLig).indices.foreach { lig =>
      (0 until Maze.getNbCol).map { col =>
        val coord = Coord(lig, col)
        val r = this.getImgView(field(coord), coord, coord == posPlayer)
        grid.children.remove(gridNodes(Coord(lig, col)))
        gridNodes = gridNodes + (Coord(lig, col) -> r)
        grid.add(r, col, lig, 1, 1)
      }
    }
  }

  private def loadMaze(): GridPane = {
    val field = maze.getGameState.field
    val posPlayer = maze.getGameState.playerPos
    val pane = new GridPane()

    (0 until Maze.getNbLig).indices.foreach { lig =>
      (0 until Maze.getNbCol).map { col =>
        val coord = Coord(lig, col)
        val r = this.getImgView(field(coord), coord, coord == posPlayer)
        gridNodes = gridNodes + (Coord(lig, col) -> r)
        pane.add(r, col, lig, 1, 1)
      }
    }
    pane
  }

  private def getImageFromPath(url: String): Image = {
    new Image(url, 30, 30, false, false)
  }

  private def getImgView(square: Square, coord: Coord, isPlayer: Boolean = false): ImageView = {
    val img = if (!isPlayer)
      new ImageView(fieldImages(square.sqType))
    else
      new ImageView(marioImg)
    Tooltip.install(img, new Tooltip(coord.toString))
    img
  }

  private def movePlayer(direction: Direction) = {
    if (maze.movePlayer(direction)) {
      import javafx.scene.control.Alert.AlertType
      new Alert(AlertType.INFORMATION) {
        title = "You won!!!"
        headerText = "You won!!!"
        contentText = "You won!!!"
      }.showAndWait()
    }
  }
}
