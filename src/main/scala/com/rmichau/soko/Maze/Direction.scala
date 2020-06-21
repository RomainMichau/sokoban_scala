package com.rmichau.soko.Maze

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
