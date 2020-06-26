package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Field, Square, SquareType}
import com.rmichau.soko.Solver.Node.{BFSNode, MoveNode, MoveNodeState}
import com.rmichau.soko.util.Util

object Deadlocks {
  def detectStaticDeadLocks(field: Field): Iterable[Square] = {
  println("calculating static deadlock")
    val ch = Util.Chrono()
    val res = field.getAllSquares
      .filter(sq => sq.sqType == SquareType.Ground)
      .filter { sq =>
        BFS.doBFS(MoveNode.getMoveNodeAsABoxInAnEmptyField(MoveNodeState(field, sq.coord)),
          new BfsPlainQueue[MoveNode],
          (node: MoveNode) => node.nodeState.getCurrentSquare.isAGoal)
          .finalNode.isEmpty
      }
    println("calculating static deadlock DONE")
    println(s"${ch.currentTime} ms")
    res
  }
}
