package com.rmichau.soko.Maze

import scala.util.hashing.MurmurHash3


case class Coord(lig: Int, col: Int){

  def getCoordAfterMove(direction: Direction): Coord = {
    direction match {
      case UP => Coord(this.lig-1, this.col)
      case DOWN => Coord(this.lig+1, this.col)
      case RIGHT => Coord(this.lig, this.col+1)
      case LEFT => Coord(this.lig, this.col-1)
    }
  }

  lazy val isInField: Boolean = {
    lig >= 0 && lig < Maze.getNbLig && col >= 0 && col < Maze.getNbCol
  }

  lazy val adjacentSq: Set[Coord] = {
    Direction.values.map(this.getCoordAfterMove)
  }

  override def toString: String = s"$lig;$col"

  override def hashCode(): Int =     MurmurHash3.listHash(List(lig, col), 2)

  override def equals(obj: Any): Boolean = {
    obj match {
      case c:Coord => c.hashCode() == this.hashCode()
      case _ => false
    }
  }

}

object Direction{
  def values = Set(UP, DOWN, LEFT, RIGHT)
}

sealed trait Direction{
  def getOpposite: Direction
  def getAdj: (Direction, Direction)
}

case object UP extends Direction {
  override def getOpposite: Direction = DOWN
  override def getAdj: (Direction, Direction) = (LEFT, RIGHT)
}
case object DOWN extends Direction {
  override def getOpposite: Direction = UP
  override def getAdj: (Direction, Direction) = (LEFT, RIGHT)
}
case object LEFT extends Direction {
  override def getOpposite: Direction = RIGHT
  override def getAdj: (Direction, Direction) = (UP, DOWN)

}
case object RIGHT extends Direction {
  override def getOpposite: Direction = LEFT
  override def getAdj: (Direction, Direction) = (UP, DOWN)
}

/**
 * Represent a Move
 * @param initialCoord Initial coord
 * @param direction direction of the move
 */
case class Move(initialCoord: Coord, direction: Direction){
  lazy val arrivalCoord: Coord = this.initialCoord.getCoordAfterMove(direction)
  lazy val getOppositeMoveCoord: Coord = this.initialCoord.getCoordAfterMove(direction.getOpposite)
}