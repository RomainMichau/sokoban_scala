package com.rmichau.soko.Maze

import com.rmichau.soko.Maze.SquareType.SquareType

class Field(
  private val field: Map[Coord, Square],
  val boxesOpt: Option[Set[Coord]] = None,
  val goalsOpt: Option[Set[Coord]] = None) {
  val boxes: Set[Coord] = boxesOpt.getOrElse(field.filter(v => v._2.isABox).keySet)
  val goals: Set[Coord] = goalsOpt.getOrElse(field.filter(v => v._2.isAGoal).keySet)

  def getBoxes: Set[Coord] = boxes
  def apply(coord: Coord): Square = field(coord)
  def +(square: Square): Field = new Field(field + (square.coord -> square), Some(boxes), Some(goals))
  def toSet: Set[(Coord, Square)] = field.toSet
  def toMap: Map[Coord, Square] = field

  def pushBox(coord: Coord, direction: Direction): Field = {
    val res = moveBox(coord, direction)
    new Field(res._1, Some(res._2), Some(goals))
  }

  def getAllSquares: Iterable[Square] = field.values

  private def moveBox(coord: Coord, direction: Direction): (Map[Coord, Square], Set[Coord]) = {
    val boxSq = field(coord)
    if (!boxSq.isABox) {
      throw new Exception(s"no box on ${coord.toString}")
    }
    val destSq = field(coord.getCoordAfterMove(direction))
    if (destSq.isABox) {
      throw new Exception(s"already a box on ${destSq.coord.toString}")
    }

    val oldSq: Square = if (boxSq.sqType == SquareType.BoxPlaced) {
      Square.goal(boxSq.coord)
    } else {
      Square.ground(boxSq.coord)
    }

    val newSq: Square = if (destSq.sqType == SquareType.Goal) {
      Square.boxPlaced(destSq.coord)
    } else {
      Square.box(destSq.coord)
    }
    val newBoxes = boxes - (oldSq.coord) + (destSq.coord)
    (field + (oldSq.coord -> oldSq, newSq.coord -> newSq), newBoxes)
  }

  def drawField(posPlayer: Option[Coord] = None): Unit = {
    def getColor(square: Square): String = {
      square.sqType match {
        case SquareType.Box | SquareType.BoxPlaced => Console.BLUE
        case SquareType.Wall => Console.MAGENTA
        case SquareType.Goal => Console.GREEN
        case SquareType.Deadlock => Console.RED
        case SquareType.Ground => Console.WHITE
      }
    }

    // Print col index
    print("   ")
    (0 until Maze.getNbCol).foreach { nb =>
      print(s"$nb ")
      if (nb < 10) print(" ")
    }
    println()

    for (lig <- 0 until Maze.getNbLig) {
      print(lig + "  ")
      for (col <- 0 until Maze.getNbCol) {
        val sq = this.field(Coord(lig, col))
        if (Coord(lig, col) != posPlayer.getOrElse(None)) {
          print(getColor(sq) + sq.sym + "  ")
        } else {
          print(Console.YELLOW + 5 + "  ")
        }
      }
      println()
    }
  }

}

object SquareType extends Enumeration {
  private val GROUND_NUM = 0
  private val WALL_NUM = 1
  private val BOX_NUM = 2
  private val PLACED_BOX_NUM = 3
  private val GOAL_NUM = 4
  private val DEADLOCK_NUM = 9
  type SquareType = Value
  val Ground = Value(GROUND_NUM)
  val Wall = Value(WALL_NUM)
  val Box = Value(BOX_NUM)
  val BoxPlaced = Value(PLACED_BOX_NUM)
  val Goal = Value(GOAL_NUM)
  val Deadlock = Value(DEADLOCK_NUM)
}

object Square {
  def box(coord: Coord): Square = Square(SquareType.Box, coord)
  def ground(coord: Coord): Square = Square(SquareType.Ground, coord)
  def wall(coord: Coord): Square = Square(SquareType.Wall, coord)
  def boxPlaced(coord: Coord): Square = Square(SquareType.BoxPlaced, coord)
  def goal(coord: Coord): Square = Square(SquareType.Goal, coord)
  def deadSquare(coord: Coord): Square = Square(SquareType.Deadlock, coord)
}

case class Square(sqType: SquareType, coord: Coord) {
  lazy val sym: Int = sqType.id
  lazy val isWalkable: Boolean = sqType match {
    case SquareType.Ground | SquareType.Deadlock | SquareType.Goal => true
    case _ => false
  }
  lazy val isABox: Boolean = sqType match {
    case SquareType.Box | SquareType.BoxPlaced => true
    case _ => false
  }
  lazy val isAGoal: Boolean = sqType match {
    case SquareType.Goal | SquareType.BoxPlaced => true
    case _ => false
  }
}
