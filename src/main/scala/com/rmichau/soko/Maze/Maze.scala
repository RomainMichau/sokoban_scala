package com.rmichau.soko.Maze

import scala.collection.immutable.HashMap
import scala.io.Source

object SquareType extends Enumeration {
  val Ground = Value(0)
  val Wall = Value(1)
  val Box = Value(2)
  val BoxPlaced = Value(3)
  val Goal = Value(4)
  val Mario = Value(5)
  val DeadSquare = Value(9)
}


class Maze {
  var currentGameState = this.loadLevelFromFile("/home/rmichau/other_projects/sc_sokoban/ressources/levels/medium/medium_2.dat")
  val (lig, col) = getNbLigNbCol()

  def field = currentGameState.field

  def posPlayer = currentGameState.playerPos

  def boxes = currentGameState.posBoxes

  val goals: Set[Coord] = this.getGoals()
  drawField()


  //def changeGameState
  private def loadLevelFromFile(levelPath: String): GameState = {
    val levelFile = Source.fromFile(levelPath)
    val fileLines = levelFile.getLines().toArray
    levelFile.close()
    val nbCol = fileLines.map(_.length).max
    var pos = Coord(0, 0)
    val field: HashMap[Coord, Square] = fileLines.zipWithIndex.map { case (li, idxli) =>
      li.toCharArray.zipWithIndex.map { case (sq, idxCol) =>
        if (sq.asDigit == 5) pos = Coord(idxli, idxCol)
        SquareType(sq.asDigit)
      } ++ Array.fill(nbCol - li.length)(SquareType.Wall)
    }
    val boxes = (for (li <- field.indices; col <- field(li).indices if field(li)(col) == SquareType.Box)
      yield Coord(li, col)).toSet
    GameState(field, pos, boxes)
  }

  private def getNbLigNbCol(): (Int, Int) = {
    (field.length, field.map(_.length).max)
  }

  private def getGoals(): Set[Coord] = {
    (for (li <- field.indices; col <- field(li).indices if field(li)(col) == SquareType.Goal)
      yield Coord(li, col)).toSet
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

    this.field.zipWithIndex.foreach { case (li, idx) =>
      // print col index
      print(Console.RESET + idx + " ")
      if (idx < 10) print(" ")
      li.foreach(sq => print(getColor(sq) + s"${sq.id}  "))
      print('\n')
    }
  }


}

case class GameState(field: HashMap[Coord, Square], playerPos: Coord, posBoxes: Set[Coord])
case class Square(coord: Coord, squareType:SquareType.Value)