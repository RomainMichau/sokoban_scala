package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Coord, Field, Maze, SquareType}
import com.rmichau.soko.Solver.Node.{MoveNode, MoveNodeState}

object SolverHelper {
  def detectZoneAccessible(field: Field, pos: Coord): Set[Coord] = {
    BFS.doBFS(
      MoveNode.getFieldNodeAsAPlayerWhoCantPushBox(MoveNodeState(field, pos)),
      new BfsPlainQueue[MoveNode],
      (_: MoveNode) => false)
      .visitedNode
      .map(_.nodeState.pos)
  }

  def getDistMap(field: Field): Map[Coord, Int] = {
    val map = field.getAllSquares.filter(_.sqType != SquareType.Wall).map { sq =>
      val node: MoveNode = MoveNode.getMoveNodeAsABoxInAnEmptyField(MoveNodeState(field, sq.coord))
      (sq.coord -> BFS.doBFS(node,
        new BfsPlainQueue[MoveNode],
        (n: MoveNode) => n.nodeState.getCurrentSquare.isAGoal)
        .finalNode
        .map(_.getPathToNode.size).getOrElse(-1))
    }.toMap

    // draw the map
    for (lig <- 0 until Maze.getNbLig) {
      print(lig + "  ")
      for (col <- 0 until Maze.getNbCol) {
        val dist = map.getOrElse((Coord(lig, col)), -1)
        print(dist+ " ")
        if(dist >= 0 && dist < 10)
          print(" ")
      }
      println()
    }
    map
  }
}
