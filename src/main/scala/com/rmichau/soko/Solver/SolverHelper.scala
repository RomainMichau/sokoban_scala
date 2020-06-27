package com.rmichau.soko.Solver

import com.rmichau.soko.Maze._
import com.rmichau.soko.Solver.Node.{MoveNode, MoveNodeState}

object SolverHelper {

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
        print(dist + " ")
        if (dist >= 0 && dist < 10) {
          print(" ")
        }
      }
      println()
    }
    map
  }

  def getPathAsAPlayerCannotPushBox(initCoord: Coord, arrivalCoord: Coord, field: Field): Option[Vector[Move]] = {
    val node: MoveNode = MoveNode.getMoveNodeAsAPlayerWhoCantPushBox(MoveNodeState(field, initCoord))
    val bfsRes = BFS.doBFS(node,
      new BfsPlainQueue[MoveNode],
      (n: MoveNode) => n.nodeState.getCurrentSquare.coord == arrivalCoord)
    val res = bfsRes.finalNode
      .map {
        _.getPathToNode
      }
      .map { t =>
        val vect = t.flatMap(_.incomingEdge.map(_.move))
        if (bfsRes.finalNode.get.incomingEdge.isDefined) {
          vect :+ bfsRes.finalNode.get.incomingEdge.get.move
        } else {vect}
      }
    res
  }
}
