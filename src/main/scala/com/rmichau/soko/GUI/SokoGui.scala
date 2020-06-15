import com.rmichau.soko.Maze.{Maze, SquareType, SquareTypeEnum}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{DoubleProperty, ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{TableCell, TableColumn, TableView}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

class SokoGui {



  def stage(maze: Maze) = new PrimaryStage {
    title = "TableView with custom color cell"
    scene = new Scene {
      content = getMaze(maze)
    }
  }

  def getMaze(maze: Maze): TableView[Seq[SquareType]] = {
    val table = new TableView[Seq[SquareType]](ObservableBuffer[Seq[SquareType]](maze.toSeq())) {
      columns ++= (0 until Maze.getNbCol).map { col =>
        new TableColumn[Seq[SquareType], String]() {
          cellValueFactory = { sq => new StringProperty(sq.value(col).sym.toString) }
          style = "-fx-background-color: yellow"  }
          cellFactory = { sq: Seq[SquareType] =>
          new TableCell[SquareType, Color] {
            item.onChange { (_, _, newColor) =>
              graphic =
                if (newColor != null)
                  new Circle {
                    fill = newColor
                    radius = 8
                  }
                else
                  null
            }
          }
      }
    }
    //table.setFixedCellSize(Maze.getNbLig)
    table
  }

  private def getStyle(square: SquareType) = {
      square.sqType match {
        case SquareTypeEnum.Box | SquareTypeEnum.BoxPlaced => "-fx-background-color: blue"
        case SquareTypeEnum.Wall =>  "-fx-background-color: magenta"
        case SquareTypeEnum.Goal => "-fx-background-color: green"
        case SquareTypeEnum.DeadSquare => "-fx-background-color: red"
        case SquareTypeEnum.Ground => "-fx-background-color: white"
      }

  }

}
