package com.rmichau.soko.Maze

import com.rmichau.soko.Maze.SquareType.SquareType



class Field(private var field: Map[Coord, Square]){
  private var boxes = field.filter(v => v._2.isABox).keySet
  val goals: Set[Coord] = field.filter(v => v._2.isAGoal).keySet

  def getBoxes: Set[Coord] = boxes
  def apply(coord: Coord): Square = field(coord)
  def +=(square: Square): Unit = field = field + (square.coord -> square)
  def toSet: Set[(Coord, Square)] = field.toSet

  def getFieldAfterPushBox(coord: Coord, direction: Direction): Field = {
    new Field(moveBox(coord, direction))
  }

  def pushBox(coord: Coord, direction: Direction): Unit = {
    field = moveBox(coord, direction)
  }

  def getAllSquares: Iterable[Square] = field.values

  private def moveBox(coord: Coord, direction: Direction): Map[Coord, Square] = {
    val boxSq = field(coord)
    if(!boxSq.isABox){
      throw new Exception(s"no box on ${coord.toString}")
    }
    val destSq = field(coord.getCoordAfterMove(direction))
    if(destSq.isABox){
      throw new Exception(s"already a box on ${destSq.coord.toString}")
    }

    val oldSq: Square = if (boxSq.sqType == SquareType.BoxPlaced) {
      Square.goal(boxSq.coord)
    }
    else {
      Square.ground(boxSq.coord)
    }

    val newSq: Square = if (destSq.sqType == SquareType.Goal) {
      Square.boxPlaced(destSq.coord)
    }
    else {
      Square.box(destSq.coord)
    }
    boxes = boxes - (oldSq.coord) + (destSq.coord)
    field + (oldSq.coord -> oldSq, newSq.coord -> newSq)
  }



}

object SquareType extends Enumeration {
  type SquareType = Value
  val Ground = Value(0)
  val Wall = Value(1)
  val Box = Value(2)
  val BoxPlaced = Value(3)
  val Goal = Value(4)
  val Deadlock = Value(9)
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