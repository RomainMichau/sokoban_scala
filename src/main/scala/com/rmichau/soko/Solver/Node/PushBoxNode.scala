package com.rmichau.soko.Solver.Node

import com.rmichau.soko.Maze.{Coord, Direction, Field, GameState, Move, Square, SquareType}
import com.rmichau.soko.Solver.{AccessibleZone, SolverHelper}

import scala.util.hashing.MurmurHash3



case class PushBoxNode(state: PushBoxNodeState,
                       moveToArrivedAtThisNode: Option[Move],
                       parentNode: Option[PushBoxNode]) extends BFSNode[PushBoxNode]{

  val field: Field = state.field

  override def getConnectedNode: Set[PushBoxNode] =  {
    this.state.accessibleZone.potentialPushBoxes.flatMap{ move =>
      val boxPos = move.arrivalCoord
      val newBoxPos = boxPos.getCoordAfterMove(move.direction)
      if(field(newBoxPos).isWalkable){
        val newField = field.pushBox(boxPos, move.direction)
        Some(PushBoxNode(PushBoxNodeState(newField, AccessibleZone(newField, boxPos)), Some(move) ,Some(this)))
      }
      else None
    }
  }


  override def equals(obj: Any): Boolean = obj match {
    case c:PushBoxNode => c.hashCode() == this.hashCode()
    case _ => false
  }

  override def hashCode(): Int = state.hashCode()

  override def draw(): Unit = field.drawField(state.accessibleZone.accessibleCoord.headOption)

  override def toString: String = this.moveToArrivedAtThisNode.map(_.toString).getOrElse("First Node")
}

case class PushBoxNodeState(field: Field, accessibleZone: AccessibleZone) {
  def this(gameState: GameState) = this(gameState.field, AccessibleZone(gameState.field, gameState.playerPos))

  override def equals(obj: Any): Boolean = {
    obj match {
      case c:PushBoxNodeState => c.hashCode() == this.hashCode()
      case _ => false
    }
  }

  override def hashCode(): Int = {
    val hashField = MurmurHash3.mapHash(field.toMap)
    val hashZone = MurmurHash3.setHash(accessibleZone.accessibleCoord)
    MurmurHash3.listHash(List(hashField, hashZone), 0)
  }
}