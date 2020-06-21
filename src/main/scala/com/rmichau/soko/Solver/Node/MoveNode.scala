package com.rmichau.soko.Solver.Node

import com.rmichau.soko.Maze.{Coord, Direction, Field, GameState, Move, Square, SquareType}

object MoveNode {
  def getMoveNodeAsABoxInAnEmptyField(nodeState: MoveNodeState) = new MoveNode(nodeState, None, moveAsABoxInAnEmptyField)
  def getFieldNodeAsAPlayerWhoCantPushBox(nodeState: MoveNodeState) = new MoveNode(nodeState, None, moveAsAPlayerWhoCannotPushBox)

  private def moveAsABoxInAnEmptyField(moveEdge: PotentialMoveEdge): Option[MoveNodeState] = {
    val field = moveEdge.field
    val coordAfterMove = moveEdge.move.arrivalCoord
    val coordPlayer = moveEdge.move.getOppositeMoveCoord
    val isMoveOk = coordAfterMove.isInField && coordPlayer.isInField && field(coordAfterMove).sqType != SquareType.Wall && field(coordPlayer).sqType != SquareType.Wall
    if (isMoveOk) {
      Some(MoveNodeState(field, coordAfterMove))
    }
    else
      None
  }

  private def moveAsAPlayerWhoCannotPushBox(moveEdge: PotentialMoveEdge): Option[MoveNodeState] = {
    val coordAfterMove = moveEdge.move.arrivalCoord
    val isMoveOk = coordAfterMove.isInField && moveEdge.field(coordAfterMove).isWalkable
    if (isMoveOk) {
      Some(MoveNodeState(moveEdge.field, coordAfterMove))
    }
    else
      None
  }
}

/**
 * This node is connected to the node which have a nodestate.pos at a dist of one
 * To get the connected Node we test all the direction and keep the acceptable node
 * This type of node consider that all the non-wall node are grounds
 *
 * @param nodeState
 * @param parentNode
 * @param isAconnectedNode
 */
case class MoveNode(nodeState: MoveNodeState,
                    parentNode: Option[MoveNode],
                    isAconnectedNode: PotentialMoveEdge => Option[MoveNodeState]) extends BFSNode[MoveNode] {
  implicit private val field: Field = nodeState.field
  private val pos = nodeState.pos

  override def getConnectedNode: Set[MoveNode] = Direction.values.flatMap(dir => isAconnectedNode(new PotentialMoveEdge(Move(pos, dir), field)))
    .map(state => MoveNode(state, Some(this), isAconnectedNode))

  override def toString: String = pos.toString

  override def hashCode(): Int = pos.hashCode()

  override def equals(obj: Any): Boolean = {
    obj match {
      case node: MoveNode => node.hashCode() == this.hashCode()
      case _ => false
    }
  }
}

class PotentialMoveEdge(val move: Move, val field: Field)

case class EdgeMove(override val move: Move,
                    override val field: Field,
                    node1: MoveNode,
                    node2: MoveNode
                   ) extends PotentialMoveEdge(move, field) with BFSEdge[MoveNode]


case class MoveNodeState(field: Field, pos: Coord) {
  def this(gameState: GameState) = this(gameState.field, gameState.playerPos)
  def getCurrentSquare: Square = field(pos)
}
