package com.rmichau.soko.GUI

import com.rmichau.soko.Maze.SquareType.SquareType
import com.rmichau.soko.Maze._
import com.rmichau.soko.Solver.BFS.BFSResult
import com.rmichau.soko.Solver.{BFS, MazeSolver}
import com.rmichau.soko.Solver.Node.PushBoxNode
import javafx.animation.{KeyFrame, Timeline}
import javafx.beans.property.SimpleObjectProperty
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.Tooltip
import javafx.util.Duration
import scalafx.Includes._
import scalafx.animation.PauseTransition
import scalafx.beans.property.ObjectProperty
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Button, Label, ProgressIndicator}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color
import scalafx.stage.Modality

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.HashMap
import scala.concurrent.Await

class SokoGui(var maze: Maze) {

  val mazeProp: ObjectProperty[Maze] = new SimpleObjectProperty[Maze](maze)
  var gridNodes: Map[Coord, ImageView] = HashMap()
  val TIME_BETWEEN_FRAME_MS: Int = 50
  val IMAGE_WIDTH: Int = 30
  val IMAGE_HEIGHT: Int = 30
  val mazeSolver: MazeSolver = new MazeSolver(maze)
  private val fieldImages: Map[SquareType, Image] = {
    val imgPath = SokoStage.imgPath
    var mp: Map[SquareType, Image] = HashMap()
    SquareType.values.foreach { sq =>
      val url = imgPath + sq.toString + ".jpg"
      val img = getImageFromPath(url)
      if (img.width == null) {
        throw new Exception(s"img $url does not exist")
      }
      mp = mp + (sq -> img)
    }
    mp
  }
  private val marioImg = getImageFromPath(
    getClass.getResource("/img/mario.jpg").toString
  )

  var grid: GridPane = loadMaze()

  val keyEventManager: KeyEvent => Unit = { key: KeyEvent =>
    if (KeyCode.Up.getCode == key.getCode.getCode) {
      this.movePlayer(UP)
    }
    if (KeyCode.Down.getCode == key.getCode.getCode) {
      this.movePlayer(DOWN)
    }
    if (KeyCode.Left.getCode == key.getCode.getCode) {
      this.movePlayer(LEFT)
    }
    if (KeyCode.Right.getCode == key.getCode.getCode) {
      this.movePlayer(RIGHT)
    }
    if (KeyCode.Escape.getCode == key.getCode.getCode) {
      maze.reinitGame()
      refreshGrid()
    }
    if (KeyCode.R.getCode == key.getCode.getCode) {
      launchSolver()
    }
  }

  private def launchSolver(): Unit = {
    RightSideCommand.ongoingGamePlay.setValue("BFS")
    RightSideCommand.ongoingGamePlay.setTextFill(Color.Red)
    val pause = new PauseTransition(Duration.millis(4)) {
      this.onFinished = { (_: ActionEvent) =>
        manageBfsResult(mazeSolver.solveMaze())
        RightSideCommand.ongoingGamePlay.setValue("Manual")
        RightSideCommand.ongoingGamePlay.setTextFill(Color.Black)
      }
    }
    pause.play()
  }

  private def field = maze.getGameState.field

  private def posPlayer: Coord = maze.getGameState.playerPos

  def stage() = {

    val scene = new Scene {
      onKeyPressed = keyEventManager
      content = grid
    }
    SokoStage.setScene(scene)
  }

  private def manageBfsResult(result: BFSResult[PushBoxNode]): Unit = {
    val finalNode = result.finalNode
    finalNode match {
      case Some(res) =>
        val dirs = res.toDirs
        RightSideCommand.nodeToFindRes.setValue(result.nodeToFindRes.toString)
        RightSideCommand.timeTofindRes.setValue(result.timeToFindRes.toString)
        RightSideCommand.resLenght.setValue(dirs.length.toString)
        this.drawMove(dirs)
      case None =>
    }
  }

  private def drawMove(dirs: Vector[Direction]): Unit = {
    val timeline = new Timeline(
      new KeyFrame(
        Duration.millis(TIME_BETWEEN_FRAME_MS),
        new EventHandler[ActionEvent]() {
          var i = 0
          override def handle(t: ActionEvent): Unit = {
            movePlayer(dirs(i))
            refreshGrid()
            i = i + 1
          }
        }
      )
    )
    timeline.setCycleCount(dirs.size)
    timeline.play()
  }

  private def refreshGrid(): Unit = {
    (0 until Maze.getNbLig).indices.foreach { lig =>
      (0 until Maze.getNbCol).map { col =>
        val coord = Coord(lig, col)
        val r = this.getImgView(field(coord), coord, coord == posPlayer)
        grid.children.remove(gridNodes(Coord(lig, col)))
        gridNodes = gridNodes + (Coord(lig, col) -> r)
        grid.add(r, col + 1, lig, 1, 1)
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
        pane.add(r, col + 1, lig, 1, 1)
      }
    }

    RightSideCommand.values.foreach { label =>
      pane.add(label, Maze.getNbCol + 1, label.row)
    }

    leftSideCommand.values.foreach { button => pane.add(button, 0, button.row) }

    pane
  }

  private def getImageFromPath(url: String): Image = {
    new Image(url, IMAGE_WIDTH, IMAGE_HEIGHT, false, false)
  }

  private def getImgView(
      square: Square,
      coord: Coord,
      isPlayer: Boolean = false
  ): ImageView = {
    val img = if (!isPlayer) {
      new ImageView(fieldImages(square.sqType))
    } else {
      new ImageView(marioImg)
    }
    Tooltip.install(img, new Tooltip(coord.toString))
    img
  }

  private def movePlayer(direction: Direction): Unit = {
    maze.movePlayer(direction)
    refreshGrid()
  }

  trait GridMember extends Node { val row: Int }

  class DescriptorLabel(
      descriptor: String,
      val row: Int,
      private var value: String = "",
      suffix: String = ""
  ) extends Label {
    this.text = s"$descriptor: $value $suffix"
    def setValue(newValue: String): Unit =
      this.setText(s"$descriptor: $newValue $suffix")
  }

  class LevelCommandButton(val row: Int) extends Button with GridMember {
    this.focusTraversable = false
  }
  class ProgressIndicatorNode(val row: Int)
      extends ProgressIndicator
      with GridMember

  object RightSideCommand {
    val levelName: DescriptorLabel =
      new DescriptorLabel("levelName", 0, maze.levelName)
    val ongoingGamePlay: DescriptorLabel =
      new DescriptorLabel("Gameplay type", 1, "manual")
    val timeTofindRes: DescriptorLabel =
      new DescriptorLabel("BFS Time", 2, suffix = "ms")
    val nodeToFindRes: DescriptorLabel =
      new DescriptorLabel("Nb nodes visited", 3)
    val resLenght: DescriptorLabel = new DescriptorLabel("Res length", 4)
    def values: Set[DescriptorLabel] =
      Set(levelName, ongoingGamePlay, timeTofindRes, resLenght, nodeToFindRes)
  }

  object leftSideCommand {
    val changeLevelButton: LevelCommandButton = new LevelCommandButton(0) {
      this.text = "change level"
      this.onAction = (_: ActionEvent) => {
        //SokoStage.stage.
        this.disable = true
        maze = new Maze(LevelPicker.pickLevel)
        mazeSolver.setMaze(maze)
        grid = loadMaze()
        stage()
        this.disable = false
      }
    }

    val solveLevelButton: LevelCommandButton = new LevelCommandButton(1) {
      this.text = "solve level"
      this.onAction = (_: ActionEvent) => {
        this.disable = true
        launchSolver()
        this.disable = false
      }
    }
    /* val progressIndicator: ProgressIndicatorNode = new ProgressIndicatorNode(2){
      this.disable = true
      this.maxHeight = IMAGE_HEIGHT
      this.maxWidth = IMAGE_WIDTH
    }*/
    val values: Set[GridMember] = Set(changeLevelButton, solveLevelButton)

  }
}
