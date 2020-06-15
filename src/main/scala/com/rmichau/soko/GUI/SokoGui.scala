import com.rmichau.soko.GUI.SokoStage
import com.rmichau.soko.Maze.SquareTypeEnum.SquareTypeEnum
import com.rmichau.soko.Maze.{Coord, Direction, Maze, SquareType, SquareTypeEnum}
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Tooltip
import scalafx.Includes._
import javafx.scene.layout.RowConstraints
import javafx.scene.shape.StrokeType
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{DoubleProperty, ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{HPos, Insets, Pos, VPos}
import scalafx.scene.Scene
import scalafx.scene.control.{Label, TableCell, TableColumn, TableView}
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, HBox, Priority}
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.Text

import scala.collection.immutable.HashMap
import scala.collection.mutable

class SokoGui(maze: Maze) {

  val mazeProp: ObjectProperty[Maze] = new SimpleObjectProperty[Maze](maze)
  var gridNodes: Map[Coord, ImageView] = HashMap()
  private val fieldImages: Map[SquareTypeEnum, Image] = {
    val imgPath = SokoStage.imgPath
    var mp: Map[SquareTypeEnum, Image] = HashMap()
    SquareTypeEnum.values.foreach{sq =>
      val url = imgPath+sq.toString+".jpg"
      val img = getImageFromPath(url)
      if(img.width == null)
        throw new Exception(s"img $url does not exist")
      mp = mp + (sq -> img)}
    mp
  }
  private val marioImg = getImageFromPath(getClass.getResource("/img/mario.jpg").toString)
  val grid = loadMaze()

  val keyEventManager = { key: KeyEvent =>
    if (KeyCode.Up.getCode == key.getCode.getCode)
      maze.movePlayer(Direction.UP)
    if (KeyCode.Down.getCode == key.getCode.getCode)
      maze.movePlayer(Direction.DOWN)
    if (KeyCode.Left.getCode == key.getCode.getCode)
      maze.movePlayer(Direction.LEFT)
    if (KeyCode.Right.getCode == key.getCode.getCode)
      maze.movePlayer(Direction.RIGHT)
    if (KeyCode.Escape.getCode == key.getCode.getCode)
      maze.reinitGame
    refreshGrid()
  }

  private def field = maze.getGameState().field

  private def posPlayer = maze.getGameState().playerPos

  def stage = {

   val scene = new Scene {
        onKeyPressed = keyEventManager
        content = grid
    }
    SokoStage.changeScene(scene)
  }

  private def refreshGrid() = {
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
    val field = maze.getGameState().field
    val posPlayer = maze.getGameState().playerPos
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

  private def getImgView(square: SquareType, coord: Coord, isPlayer: Boolean = false): ImageView = {
    val img = if(!isPlayer)
      new ImageView(fieldImages(square.sqType))
    else
     new ImageView(marioImg)
    Tooltip.install(img, new Tooltip(coord.toString))
    img
  }
/*
  private def getRect(square: SquareType, coord: Coord, isPlayer: Boolean = false): Rectangle = {
    val r = new Rectangle {
      width = 30
      height = 30
      strokeType = StrokeType.INSIDE
      strokeWidth = 0.7
      stroke = Color.Black
      fill = if (!isPlayer)
        square.sqType match {
          case SquareTypeEnum.Box => Color.Blue
          case SquareTypeEnum.BoxPlaced => Color.GreenYellow
          case SquareTypeEnum.Wall => Color.Magenta
          case SquareTypeEnum.Goal => Color.Green
          case SquareTypeEnum.DeadSquare => Color.Red
          case SquareTypeEnum.Ground => Color.White
        }
      else
        Color.Yellow
    }
    Tooltip.install(r, new Tooltip(coord.toString))
    r
  }*/
}
