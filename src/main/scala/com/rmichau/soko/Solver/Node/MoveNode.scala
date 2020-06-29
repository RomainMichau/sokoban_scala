package com.rmichau.soko.Solver.Node

import com.rmichau.soko.Maze.{ Coord, Direction, Field, GameState, Move, Square, SquareType }

object MoveNode {
  def getMoveNodeAsABoxInAnEmptyField(nodeState: MoveNodeState): MoveNode = new MoveNode(nodeState, None, moveAsABoxInAnEmptyField)
  def getMoveNodeAsAPlayerWhoCantPushBox(nodeState: MoveNodeState): MoveNode = new MoveNode(nodeState, None, moveAsAPlayerWhoCannotPushBox)
  def getMoveNodeAsAPlayerInAmEmptyField(nodeState: MoveNodeState): MoveNode = new MoveNode(nodeState, None, moveAsAPlayerInAnEmptyField)

  private def moveAsABoxInAnEmptyField(moveEdge: PotentialMoveEdge): Option[MoveNodeState] = {
    val field = moveEdge.field
    val coordAfterMove = moveEdge.move.arrivalCoord
    val coordPlayer = moveEdge.move.getOppositeMoveCoord
    val isMoveOk = coordAfterMove.isInField && coordPlayer.isInField && field(coordAfterMove).sqType !=
      SquareType.Wall && field(coordPlayer).sqType != SquareType.Wall
    if (isMoveOk) {
      Some(MoveNodeState(field, coordAfterMove))
    } else {
      None
    }
  }

  private def moveAsAPlayerWhoCannotPushBox(moveEdge: PotentialMoveEdge): Option[MoveNodeState] = {
    val coordAfterMove = moveEdge.move.arrivalCoord
    if (coordAfterMove.isInField && moveEdge.field(coordAfterMove).isWalkable) {
      Some(MoveNodeState(moveEdge.field, coordAfterMove))
    } else {
      None
    }
  }

  private def moveAsAPlayerInAnEmptyField(moveEdge: PotentialMoveEdge): Option[MoveNodeState] = {
    val coordAfterMove = moveEdge.move.arrivalCoord
    val isMoveOk = coordAfterMove.isInField && moveEdge.field(coordAfterMove).sqType != SquareType.Wall
    if (isMoveOk) {
      Some(MoveNodeState(moveEdge.field, coordAfterMove))
    } else {
      None
    }
  }
}

/**
 * This node is connected to the node which have a nodestate.pos at a dist of one
 * To get the connected Node we test all the direction and keep the acceptable node
 * This type of node consider that all the non-wall node are grounds
 *
 * @param nodeState
 * @param isAconnectedNode
 */
case class MoveNode(
  nodeState: MoveNodeState,
  incomingEdge: Option[EdgeMove],
  isAconnectedNode: PotentialMoveEdge => Option[MoveNodeState]) extends BFSNode[MoveNode] {
  implicit private val field: Field = nodeState.field
  private val pos = nodeState.pos

  override def getConnectedNode: Set[MoveNode] = Direction.values.flatMap { dir =>
    val move = Move(pos, dir)
    isAconnectedNode(new PotentialMoveEdge(move, field)).map {
      state => MoveNode(state, Some(EdgeMove(move, this)), isAconnectedNode)
    }
  }

  def getIncommingEdge: EdgeMove = incomingEdge.getOrElse(throw new Exception(s"node $toString does not have an incoming edge"))

  override def toString: String = pos.toString

  override def hashCode(): Int = pos.hashCode()

  override def equals(obj: Any): Boolean = {
    obj match {
      case node: MoveNode => node.hashCode() == this.hashCode()
      case _ => false
    }
  }

  override def draw(): Unit = print("")

  override def details: String = ???
}

class PotentialMoveEdge(val move: Move, val field: Field)

case class EdgeMove(
  move: Move,
  override val parentNode: MoveNode) extends BFSIncomingEdge[MoveNode]

case class MoveNodeState(field: Field, pos: Coord) {
  def this(gameState: GameState) = this(gameState.field, gameState.playerPos)
  def getCurrentSquare: Square = field(pos)
}
