package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Field, Square, SquareType}
import com.rmichau.soko.Solver.Node.{BFSNode, MoveNode, MoveNodeState}

object Deadlocks {
  def detectStaticDeadLocks(field: Field): Iterable[Square] = {
    field.getAllSquares
      .filter(sq => sq.sqType == SquareType.Ground)
      .filter { sq =>
        BFS.doBFS(MoveNode.getMoveNodeAsABoxInAnEmptyField(MoveNodeState(field, sq.coord)),
          new BfsPlainQueue[MoveNode],
          (node: MoveNode) => node.nodeState.getCurrentSquare.isAGoal)
          .finalNode.isEmpty
      }

  }
}
