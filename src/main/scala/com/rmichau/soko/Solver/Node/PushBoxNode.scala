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

  if(initialPos.isDefined && incomingEdge.isDefined) {
    throw new Exception("initialPos and incomingEdge are both defined. Only one of them must be defined")
  }

  lazy val currentPos: Coord = incomingEdge.map(_.incommingMove.arrivalCoord).getOrElse(initialPos.getOrElse(throw new Exception("no inital coord defined")))

  def field: Field = state.field

  override def getConnectedNode: Set[PushBoxNode] =  {
    this.state.accessibleZone.potentialPushBoxes.flatMap{ move =>
      val boxPos = move.arrivalCoord
      val newBoxPos = boxPos.getCoordAfterMove(move.direction)
      if(field(newBoxPos).isWalkable && field(newBoxPos).sqType != SquareType.Deadlock){
        val newField = field.pushBox(boxPos, move.direction)
        if(!isADynamicDeadLock(newBoxPos)){
          Some(PushBoxNode(PushBoxNodeState(newField, AccessibleZone(newField, boxPos)), Some(PushBoxEdge(this, move))))
        }
        else{
          None
        }
      }
      else {
          None
      }
    }
  }

  private def isADynamicDeadLock(newBox: Coord): Boolean = {
    var alreadyTestCoord: Set[Coord] = Set()
    def isBoxBlocked(box: Coord): Boolean = {
      alreadyTestCoord += box
      val boxDirectlyPushable = Direction.values.exists { dir =>
        val frontCoord = box.getCoordAfterMove(dir)
        val behindCoord = box.getCoordAfterMove(dir.getOpposite)
        frontCoord.isInField && behindCoord.isInField &&
          state.accessibleZone.accessibleCoord(behindCoord) && field(frontCoord).isWalkable && field(behindCoord).isWalkable
      }
      val adjacentBoxIsPushable = box.adjacentSq.filter{ adjCoord =>
        adjCoord.isInField &&
          field(adjCoord).isABox &&
          !alreadyTestCoord.contains(adjCoord)
      }.exists(!isBoxBlocked(_))
      !boxDirectlyPushable && !adjacentBoxIsPushable
    }

    val res = field(newBox).sqType != SquareType.BoxPlaced && isBoxBlocked(newBox)
   /* if(res) {
      field.drawField()
      print(res)
    }*/
    res
  }

  def toDirs: immutable.Vector[Direction] = {
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

  def details: String = s"boxes: ${field.getBoxes}, nPos ${state.accessibleZone.nPos}"
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
    val hashField = MurmurHash3.setHash(field.getBoxes + accessibleZone.nPos)
    MurmurHash3.listHash(List(hashField), 1)
  }
}

case class PushBoxEdge(parentNode: PushBoxNode, incommingMove: Move) extends BFSIncomingEdge[PushBoxNode]
