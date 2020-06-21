package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Coord, Field}
import com.rmichau.soko.Solver.Node.{PushBoxNode, PushBoxNodeState}

object MazeSolver {
  def solveMaze(field: Field, pos: Coord): BFS.BFSResult[PushBoxNode] = {
    val node: PushBoxNode = PushBoxNode(PushBoxNodeState(field, pos), None)
    BFS.doBFS(node,
      new BfsPlainQueue[PushBoxNode],
      (node: PushBoxNode) => node.field.getBoxes == node.field.goals)
  }
}
