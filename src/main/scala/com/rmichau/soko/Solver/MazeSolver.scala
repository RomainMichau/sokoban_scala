package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.Maze
import com.rmichau.soko.Solver.Node.{PushBoxNode, PushBoxNodeState}

class MazeSolver(maze: Maze) {

  private val distMap = SolverHelper.getDistMap(maze.field)
  def diff(node: PushBoxNode): Int= {
    val field = node.field
    val boxPlaced = field.getBoxes.intersect(field.goals)
    val dist = node.field.getBoxes.map(box => distMap(box)).sum
    -dist
  }

  def solveMaze(): BFS.BFSResult[PushBoxNode] = {
    val node: PushBoxNode = PushBoxNode(PushBoxNodeState(maze.field,AccessibleZone(maze.field, maze.posPlayer)), maze.posPlayer)
    BFS.doBFS(node,
      new BfsPriorityQueue[PushBoxNode](Ordering.by(diff)),
      (node: PushBoxNode) => node.field.getBoxes == node.field.goals,
      disp = true)
  }
}
