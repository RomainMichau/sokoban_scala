package com.rmichau.soko.Solver.Node

import com.rmichau.soko.Maze.{Coord, Direction, Field, GameState, Square, SquareType}

import scala.util.hashing.MurmurHash3



case class PushBoxNode(state: PushBoxNodeState,
                       parentNode: Option[PushBoxNode]) extends BFSNode[PushBoxNode]{

  val field: Field = state.field
  val pos: Coord = state.pos

  override def getConnectedNode: Set[PushBoxNode] =  {
    val conNode = Direction.values.flatMap { dir =>
      val newSq = field(pos.getCoordAfterMove(dir))
      val res = if (newSq.isWalkable) {
        Some(PushBoxNode(PushBoxNodeState(field, newSq.coord), Some(this)))
      }
      else if (newSq.isABox) {
        val newBoxSq = field(newSq.coord.getCoordAfterMove(dir))
        if (newBoxSq.isWalkable && newBoxSq.sqType != SquareType.Deadlock) {
          Some(PushBoxNode(PushBoxNodeState(field.getFieldAfterPushBox(newSq.coord, dir), newSq.coord), Some(this)))
        }
        else None
      }
      else None
      res
    }
    conNode
  }

  override def equals(obj: Any): Boolean = obj match {
    case c:PushBoxNode => c.hashCode() == this.hashCode()
    case _ => false
  }

  override def hashCode(): Int = state.hashCode()
}

case class PushBoxNodeState(field: Field, pos: Coord) {
  def this(gameState: GameState) = this(gameState.field, gameState.playerPos)

  override def equals(obj: Any): Boolean = {
    obj match {
      case c:PushBoxNodeState => c.hashCode() == this.hashCode()
      case _ => false
    }
  }

  override def hashCode(): Int = {
    val sq: (Coord, Square) = (pos, Square.wall(pos))
    MurmurHash3.setHash(field.toSet + sq )
  }
}