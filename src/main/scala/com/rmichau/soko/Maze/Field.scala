package com.rmichau.soko.Maze

import com.rmichau.soko.Maze.SquareType.SquareTypeEnum



class Field(private var field: Map[Coord, Square]){
  def apply(coord: Coord): Square = field(coord)
  def +=(square: Square): Unit = field = field + (square.coord -> square)
  def toSet: Set[(Coord, Square)] = field.toSet

  def getFieldAfterPushBox(coord: Coord, direction: Direction): Field = {
    new Field(moveBox(coord, direction))
  }

  def pushBox(coord: Coord, direction: Direction): Unit = {
    field = moveBox(coord, direction)
  }

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
    field +(oldSq.coord -> oldSq, newSq.coord -> newSq)
  }
}

object SquareType extends Enumeration {
  type SquareTypeEnum = Value
  val Ground = Value(0)
  val Wall = Value(1)
  val Box = Value(2)
  val BoxPlaced = Value(3)
  val Goal = Value(4)
  val DeadSquare = Value(9)
}

object Square {
  def box(coord: Coord): Square = Square(SquareType.Box, coord)
  def ground(coord: Coord): Square = Square(SquareType.Ground, coord)
  def wall(coord: Coord): Square = Square(SquareType.Wall, coord)
  def boxPlaced(coord: Coord): Square = Square(SquareType.BoxPlaced, coord)
  def goal(coord: Coord): Square = Square(SquareType.Goal, coord)
  def deadSquare(coord: Coord): Square = Square(SquareType.DeadSquare, coord)
}

case class Square(sqType: SquareTypeEnum, coord: Coord) {
  lazy val sym = sqType.id
  lazy val isWalkable = sqType match {
    case SquareType.Ground | SquareType.DeadSquare | SquareType.Goal => true
    case _ => false
  }
  lazy val isABox = sqType match {
    case SquareType.Box | SquareType.BoxPlaced => true
    case _ => false
  }
}