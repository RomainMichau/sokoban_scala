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

