package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.Maze
import com.rmichau.soko.Solver.Node.{PushBoxNode, PushBoxNodeState}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MazeSolver(private var maze: Maze) {

  def setMaze(newMaze: Maze): Unit = {
    this.maze = newMaze
    distMap = SolverHelper.getDistMap(maze.field)
  }

  private var distMap = SolverHelper.getDistMap(maze.field)

  def diff(node: PushBoxNode): Int = {
    val field = node.field
    val boxPlaced = field.getBoxes.intersect(field.goals)
    val dist = node.field.getBoxes.map(box => distMap(box)).sum
    val currentBoxDist = node.incomingEdge
      .map(edge => distMap(edge.incommingMove.arrivalCoord))
      .getOrElse(0)
    -dist - currentBoxDist + boxPlaced.size * 20
  }

  def solveMaze(): BFS.BFSResult[ PushBoxNode] = {
    println(s"solving maze ${maze.levelName}")
    val node: PushBoxNode = PushBoxNode(
      PushBoxNodeState(maze.field, AccessibleZone(maze.field, maze.posPlayer)),
      maze.posPlayer
    )
    BFS.doBFS(
      node,
      new BfsPriorityQueue[PushBoxNode](Ordering.by(diff)),
      (node: PushBoxNode) => node.field.getBoxes == node.field.goals,
      disp = true,
      bfsDecription = maze.levelName
    )
  }

}
