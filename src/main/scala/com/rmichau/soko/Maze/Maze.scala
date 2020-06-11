package com.rmichau.soko.Maze

import com.rmichau.soko.Maze.Maze.Field
import com.rmichau.soko.Maze.SquareType.SquareType

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.io.{BufferedSource, Source}

object SquareType extends Enumeration {
  type  SquareType = Value
  val Ground = Value(0)
  val Wall = Value(1)
  val Box = Value(2)
  val BoxPlaced = Value(3)
  val Goal = Value(4)
  val Mario = Value(5)
  val DeadSquare = Value(9)
}

object Maze{
  type Field = HashMap[Coord, SquareType]
}
class Maze {
  // if you use intelliJ you need to configure the resource file in the project struct
  // see: https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000168244/comments/115000201030
  val fileStream = getClass.getResourceAsStream("/levels/medium/medium_2.dat")
  var(currentGameState, lig, col) = loadLevelFromFile(Source.fromInputStream(fileStream))

  def field = currentGameState.field

  def posPlayer = currentGameState.playerPos

  def boxes = currentGameState.posBoxes

  val goals: Set[Coord] = this.getGoals()
  drawField()


  def changeGameState(gameState: GameState) = {
    this.currentGameState = gameState
  }

  /*def isSquareWalkable(coord: Coord): Boolean ={
  }*/

  private def loadLevelFromFile(level: BufferedSource): (GameState, Int, Int) = {
    val fileLines = level.getLines().toArray
    val nbCol = fileLines.map(_.length).max
    val lig = fileLines.length
    var pos = Coord(0, 0)
    val fieldArray: Array[Array[SquareType]] = fileLines.zipWithIndex.map { case (li, idxli) =>
      li.toCharArray.zipWithIndex.map { case (sq, idxCol) =>
        if (sq.asDigit == 5) pos = Coord(idxli, idxCol)
        SquareType(sq.asDigit)
      } ++ Array.fill(nbCol - li.length)(SquareType.Wall)
    }

    var fieldMap: Field = HashMap()
    fieldArray.zipWithIndex.map { case (li, idxli) =>
      li.zipWithIndex.map { case (sq, idxCol) => fieldMap = fieldMap + (Coord(idxli, idxCol) -> sq) }
    }

    val boxes = (for (li <- fieldArray.indices; col <- fieldArray(li).indices if fieldArray(li)(col) == SquareType.Box)
      yield Coord(li, col)).toSet

    (GameState(fieldMap, pos, boxes), lig ,nbCol)
  }

  private def getGoals(): Set[Coord] = {
    (for (li <- field.toSet if li._2 == SquareType.Goal)
      yield li._1).toSet
  }

  private def drawField() = {

    def getColor(square: SquareType.Value): String = {
      square match {
        case SquareType.Box | SquareType.BoxPlaced => Console.BLUE
        case SquareType.Wall => Console.MAGENTA
        case SquareType.Goal => Console.GREEN
        case SquareType.Mario => Console.YELLOW
        case SquareType.DeadSquare => Console.RED
        case SquareType.Ground => Console.WHITE
      }
    }

    // Print col index
    print("   ")
    (0 until this.col).foreach { nb =>
      print(s"$nb ")
      if (nb < 10) print(" ")
    }
    println()

    for(lig <- 0 until this.lig) {
      print(lig + "  ")
      for(col <- 0 until this.col){
        val sq = this.field(Coord(lig, col))
        print(getColor(sq) + sq.id + "  ")
      }
      println()
    }
  }
}

case class GameState(field: Field, playerPos: Coord, posBoxes: Set[Coord])