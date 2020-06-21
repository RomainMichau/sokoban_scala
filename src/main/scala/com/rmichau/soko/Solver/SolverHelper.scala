package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Coord, Field}
import com.rmichau.soko.Solver.Node.{MoveNode, MoveNodeState}

object SolverHelper {
  def detectZoneAccessible(field: Field, pos: Coord): Set[Coord]= {
    BFS.doBFS(
      MoveNode.getMoveNodeAsABoxInAnEmptyField(MoveNodeState(field, pos)),
      new BfsPlainQueue[MoveNode],
      (_: MoveNode) => false)
      .visitedNode
      .map(_.nodeState.pos)
  }
}
