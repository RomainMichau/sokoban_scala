import com.rmichau.soko.Maze.{Coord, Direction, Maze, SquareType, SquareTypeEnum}
import javafx.beans.property.SimpleObjectProperty
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
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, HBox, Priority}
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.Text

import scala.collection.immutable.HashMap
import scala.collection.mutable

class SokoGui(maze: Maze) {

  val mazeProp : ObjectProperty[Maze] = new SimpleObjectProperty[Maze](maze)
  var gridNodes:  Map[Coord, Rectangle] = HashMap()
  val grid = loadMaze()


  private def field = maze.getGameState().field
  private def posPlayer = maze.getGameState().playerPos

  def stage = {
    new PrimaryStage() {
      title = "Soko"
      scene = new Scene {
        onKeyPressed = { key =>
          if (KeyCode.Up.getCode == key.getCode.getCode) {
            maze.movePlayer(Direction.UP)
          }
          if (KeyCode.Down.getCode == key.getCode.getCode)
            maze.movePlayer(Direction.DOWN)
          if (KeyCode.Left.getCode == key.getCode.getCode)
            maze.movePlayer(Direction.LEFT)
          if (KeyCode.Right.getCode == key.getCode.getCode)
            maze.movePlayer(Direction.RIGHT)
          refreshGrid()
        }
        content = grid
      }
    }
  }

  private def refreshGrid() = {
    (0 until Maze.getNbLig).indices.foreach { lig =>
      (0 until Maze.getNbCol).map { col =>
        val coord = Coord(lig, col)
        val r =  this.getRect(field(coord), coord == posPlayer)
        grid.children.remove(gridNodes(Coord(lig,col)))
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
        val r =  this.getRect(field(coord), coord == posPlayer)
        gridNodes = gridNodes + (Coord(lig, col) -> r)
        pane.add(r, col, lig, 1, 1)
      }
    }
    pane
  }

  private def getRect(square: SquareType, isPlayer: Boolean = false): Rectangle =new Rectangle {
    width = 30
    height = 30
    strokeType = StrokeType.INSIDE
    strokeWidth = 0.7
    stroke = Color.Black
    fill =   if(!isPlayer)
      square.sqType match {
        case SquareTypeEnum.Box | SquareTypeEnum.BoxPlaced => Color.Blue
        case SquareTypeEnum.Wall => Color.Magenta
        case SquareTypeEnum.Goal => Color.Green
        case SquareTypeEnum.DeadSquare => Color.Red
        case SquareTypeEnum.Ground => Color.White
      }
    else
      Color.Yellow
  }
}
