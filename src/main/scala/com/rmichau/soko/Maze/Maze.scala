package com.rmichau.soko.Maze

import java.net.URI

import com.rmichau.soko.Maze.Direction.Direction
import com.rmichau.soko.Maze.Maze.Field
import com.rmichau.soko.Maze.SquareTypeEnum.SquareTypeEnum

import scala.collection.{immutable, mutable}
import scala.io.{BufferedSource, Source}

object Direction extends Enumeration {
  type Direction = Value
  val UP = Value(0)
  val DOWN = Value(1)
  val LEFT = Value(2)
  val RIGHT = Value(3)
}

object Maze {
  type Field = mutable.HashMap[Coord, SquareType]
  private var nbLig = 0
  private var nbCol = 0

  def getNbLig = nbLig

  def getNbCol = nbCol
}

class Maze(filePath: URI) {
  // if you use intelliJ you need to configure the resource file in the project struct
  // see: https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000168244/comments/115000201030
  private var currentGameState = loadLevelFromFile(filePath)

  private def field: Field = currentGameState.field

  private def posPlayer: Coord = currentGameState.playerPos

  private def boxes: Set[Coord] = currentGameState.posBoxes

  private def nbCol: Int = Maze.getNbCol

  private def nbLig: Int = Maze.getNbLig


  val goals: Set[Coord] = this.getGoals()
  drawField()

  def getGameState(): GameState = currentGameState


  def movePlayer(direction: Direction): Boolean = {
    val dest = posPlayer.getCoordAfterMove(direction)
    if (dest.isInField()) {
      val destSq = field(dest)
      if (destSq.isWalkable) {
        this.currentGameState.playerPos = dest
        //this.drawField()
        hasWon
      }
      else if (destSq.isABox) {
        if (this.pushBox(dest, direction)) {
          this.currentGameState.playerPos = dest
          this.drawField()
        }
        hasWon
      }
      hasWon
    }
    hasWon
  }

  private def hasWon() = {
    val wom = this.getGoals() == this.boxes
    if(wom){
      println(wom)
    }
    wom
  }

  private def pushBox(boxCoord: Coord, direction: Direction): Boolean = {
    canPushBox(boxCoord, direction).exists { dest =>
      val boxSq = field(boxCoord)
      val destSq = field(dest)
      currentGameState.posBoxes = boxes - boxCoord
      currentGameState.posBoxes = boxes + dest
      if (boxSq.sqType == SquareTypeEnum.BoxPlaced) {
        currentGameState.field(boxCoord) = SquareType.goal
      }
      else {
        currentGameState.field(boxCoord) = SquareType.ground
      }
      if (destSq.sqType == SquareTypeEnum.Goal) {
        currentGameState.field(dest) = SquareType.boxPlaced
      }
      else {
        currentGameState.field(dest) = SquareType.box
      }
      true
    }
  }

  def reinitGame = this.currentGameState = loadLevelFromFile(filePath)

  private def canPushBox(boxCoord: Coord, direction: Direction): Option[Coord] = {
    val dest = boxCoord.getCoordAfterMove(direction)
    val destsq = field(dest)
    val sq = field(boxCoord)
    if (sq.isABox && dest.isInField() && destsq.isWalkable)
      Some(dest)
    else
      None
  }

  def changeGameState(gameState: GameState) = {
    this.currentGameState = gameState
  }

  private def loadLevelFromFile(filePath: URI): GameState = {
    val fileLines = Source.fromFile(filePath).getLines().toList
    Maze.nbCol = fileLines.map(_.length).max
    Maze.nbLig = fileLines.length
    var pos = Coord(0, 0)
    val fieldArray: Array[Array[SquareType]] = fileLines.zipWithIndex.map { case (li, idxli) =>
      li.toCharArray.zipWithIndex.map { case (sq, idxCol) =>
        if (sq.asDigit == 5) {
          pos = Coord(idxli, idxCol)
          SquareType(SquareTypeEnum.Ground)
        }
        else {
          SquareType(SquareTypeEnum(sq.asDigit))
        }
      } ++ Array.fill(Maze.nbCol - li.length)(SquareType(SquareTypeEnum.Wall))
    }.toArray

    var fieldMap: Field = mutable.HashMap()
    fieldArray.zipWithIndex.map { case (li, idxli) =>
      li.zipWithIndex.map { case (sq, idxCol) => fieldMap += (Coord(idxli, idxCol) -> sq) }
    }

    val boxes = (for (li <- fieldArray.indices; col <- fieldArray(li).indices if fieldArray(li)(col).sqType == SquareTypeEnum.Box)
      yield Coord(li, col)).toSet

    GameState(fieldMap, pos, boxes)
  }

  private def getGoals(): Set[Coord] = {
    (for (li <- field.toSet if li._2.sqType == SquareTypeEnum.Goal)
      yield li._1)
  }

  private def drawField() = {

    def getColor(square: SquareType): String = {
      square.sqType match {
        case SquareTypeEnum.Box | SquareTypeEnum.BoxPlaced => Console.BLUE
        case SquareTypeEnum.Wall => Console.MAGENTA
        case SquareTypeEnum.Goal => Console.GREEN
        case SquareTypeEnum.DeadSquare => Console.RED
        case SquareTypeEnum.Ground => Console.WHITE
      }
    }

    // Print col index
    print("   ")
    (0 until this.nbCol).foreach { nb =>
      print(s"$nb ")
      if (nb < 10) print(" ")
    }
    println()

    for (lig <- 0 until this.nbLig) {
      print(lig + "  ")
      for (col <- 0 until this.nbCol) {
        val sq = this.field(Coord(lig, col))
        if (Coord(lig, col) != posPlayer)
          print(getColor(sq) + sq.sym + "  ")
        else print(Console.YELLOW + 5 + "  ")
      }
      println()
    }
  }
}

case class GameState(var field: Field, var playerPos: Coord, var posBoxes: Set[Coord])


object SquareTypeEnum extends Enumeration {
  type SquareTypeEnum = Value
  val Ground = Value(0)
  val Wall = Value(1)
  val Box = Value(2)
  val BoxPlaced = Value(3)
  val Goal = Value(4)
  val DeadSquare = Value(9)
}


object SquareType {
  def box: SquareType = SquareType(SquareTypeEnum.Box)

  def ground: SquareType = SquareType(SquareTypeEnum.Ground)

  def wall: SquareType = SquareType(SquareTypeEnum.Wall)

  def boxPlaced: SquareType = SquareType(SquareTypeEnum.BoxPlaced)

  def goal: SquareType = SquareType(SquareTypeEnum.Goal)

  def deadSquare: SquareType = SquareType(SquareTypeEnum.DeadSquare)
}

case class SquareType(val sqType: SquareTypeEnum) {
  lazy val sym = sqType.id
  lazy val isWalkable = sqType match {
    case SquareTypeEnum.Ground | SquareTypeEnum.DeadSquare | SquareTypeEnum.Goal => true
    case _ => false
  }
  lazy val isABox = sqType match {
    case SquareTypeEnum.Box | SquareTypeEnum.BoxPlaced => true
    case _ => false
  }
}
