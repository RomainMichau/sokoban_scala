package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Coord, Field, Move}
import com.rmichau.soko.Solver.Node.{MoveNode, MoveNodeState}

object AccessibleZone{
  def apply(field: Field, pos: Coord): AccessibleZone = {
    val accessibleCoord: Set[Coord] = BFS.doBFS(
      MoveNode.getMoveNodeAsAPlayerWhoCantPushBox(MoveNodeState(field, pos)),
      new BfsPlainQueue[MoveNode],
      (_: MoveNode) => false)
      .visitedNode
      .map(_.nodeState.pos)
    val accessibleBoxes: Set[Move] = field.boxes.flatMap{coord =>
      coord.adjacentSq.filter(accessibleCoord(_)).map(adjCoord => adjCoord.moveToGoToCoord(coord))
      }
    AccessibleZone(accessibleCoord, accessibleBoxes)
  }
}
case class AccessibleZone(accessibleCoord: Set[Coord], potentialPushBoxes: Set[Move])


