package com.rmichau.soko.Maze

import com.rmichau.soko.Maze.Direction.Direction

case class Coord(lig: Int, col: Int){
  def getCoordAfterMove(direction: Direction): Coord = {
    direction match {
      case Direction.UP => Coord(this.lig-1, this.col)
      case Direction.DOWN => Coord(this.lig+1, this.col)
      case Direction.RIGHT => Coord(this.lig, this.col+1)
      case Direction.LEFT => Coord(this.lig, this.col-1)
    }
  }

  def isInField() : Boolean = {
    lig >= 0 && lig < Maze.getNbLig && col >= 0 && col < Maze.getNbCol
  }
}

