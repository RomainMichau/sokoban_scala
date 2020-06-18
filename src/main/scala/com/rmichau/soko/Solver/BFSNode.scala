package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Coord, Direction, Field, GameState, SquareType}

import scala.collection.mutable

trait BFSNode {
  def getNeighbourNode: Set[BFSNode]
  def isGoalNode: Boolean
  val parentNode: Option[BFSNode]
  def getPathToNode: List[BFSNode] = {
    parentNode match {
      case Some(parentNode) => parentNode :: parentNode.getPathToNode
      case None => Nil
    }
  }
}




object FieldNode{

  def getFieldNodeAsABoxInAnEmptyField(field: Field, pos: Coord) = new FieldNode(field, pos, None, getNeighboursField(moveAsABoxInAnEmptyField))

  private def moveAsABoxInAnEmptyField(initialCoord: Coord, direction: Direction,field: Field) : Boolean = {
    val coordAfterMove = initialCoord.getCoordAfterMove(direction)
    val coordPlayer = initialCoord.getCoordAfterMove(direction.getOpposite)
    coordAfterMove.isInField && coordPlayer.isInField && field(coordAfterMove).sqType != SquareType.Wall && field(coordPlayer).sqType != SquareType.Wall
  }

  private def getNeighboursField(isANeigbourg: (Coord, Direction, Field) => Boolean)(node: FieldNode): Set[(Field, Coord)] = {
    Direction.values
      .filter(dir => isANeigbourg(node.pos, dir, node.field))
      .map(dir =>   (node.field, node.pos.getCoordAfterMove(dir)))
  }
}
class FieldNode(val field: Field,
                val pos: Coord,
                val parentNode: Option[FieldNode],
                neighboursFieldGenerator: (FieldNode) => Set[(Field, Coord)]) extends BFSNode {
  override def getNeighbourNode: Set[BFSNode] = neighboursFieldGenerator(this)
    .map(t => new FieldNode(t._1, t._2, Some(this), neighboursFieldGenerator))

  override def isGoalNode: Boolean = field(pos).sqType == SquareType.Goal

  override def toString: String = pos.toString

  override def hashCode(): Int = pos.hashCode()

  override def equals(obj: Any): Boolean = {
    obj match {
      case node: FieldNode => node.hashCode() == this.hashCode()
      case _ => false
    }
  }
}
