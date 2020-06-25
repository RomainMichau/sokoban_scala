package com.rmichau.soko.Solver.Node

import com.rmichau.soko.Maze.{Coord, Direction, Field, GameState, Move, Square, SquareType}
import com.rmichau.soko.Solver.{AccessibleZone, SolverHelper}

import scala.collection.immutable
import scala.util.hashing.MurmurHash3


object PushBoxNode{
  def apply(state: PushBoxNodeState, initialPos: Coord): PushBoxNode = new PushBoxNode(state, None, Some(initialPos))
}

/**
 * Represent a node in a bfs where we consider that the player can move in all the accessible zone in one shot
 * @param state
 * @param incomingEdge
 * @param initialPos is only for the first node
 */
case class PushBoxNode private(state: PushBoxNodeState,
                       incomingEdge: Option[PushBoxEdge],
                       initialPos: Option[Coord] = None) extends BFSNode[PushBoxNode]{

  if(initialPos.isDefined && incomingEdge.isDefined)
    throw new Exception("initialPos and incomingEdge are both defined. Only one of them must be defined")

  lazy val currentPos: Coord = incomingEdge.map(_.incommingMove.arrivalCoord).getOrElse(initialPos.getOrElse(throw new Exception("no inital coord defined")))

  val field: Field = state.field

  override def getConnectedNode: Set[PushBoxNode] =  {
    this.state.accessibleZone.potentialPushBoxes.flatMap{ move =>
      val boxPos = move.arrivalCoord
      val newBoxPos = boxPos.getCoordAfterMove(move.direction)
      if(field(newBoxPos).isWalkable){
        val newField = field.pushBox(boxPos, move.direction)
        Some(PushBoxNode(PushBoxNodeState(newField, AccessibleZone(newField, boxPos)), Some(PushBoxEdge(this, move))))
      }
      else None
    }
  }

  def toDirs(): immutable.Vector[Direction] = {
    (this.getPathToNode.drop(1) :+ this).flatMap{ node =>
      SolverHelper.getPathAsAPlayerCannotPushBox(node.parentNode.get.currentPos, node.incomingEdge.get.incommingMove.initialCoord, node.parentNode.get.field)
        .map(_ :+ node.incomingEdge.get.incommingMove)
        .get.map(_.direction)
    }
  }

  override def equals(obj: Any): Boolean = obj match {
    case c:PushBoxNode => c.hashCode() == this.hashCode()
    case _ => false
  }

  override def hashCode(): Int = state.hashCode()

  override def draw(): Unit = field.drawField(state.accessibleZone.accessibleCoord.headOption)

  override def toString: String = this.incomingEdge.map(edge => edge.incommingMove.initialCoord.toString + edge.incommingMove.direction)
    .getOrElse("First Node")
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

case class PushBoxEdge(parentNode: PushBoxNode, incommingMove: Move) extends BFSIncomingEdge[PushBoxNode]
