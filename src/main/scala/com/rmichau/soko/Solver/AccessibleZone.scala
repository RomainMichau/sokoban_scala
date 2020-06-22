package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Coord, Field, Move}
import com.rmichau.soko.Solver.Node.{MoveNode, MoveNodeState}

object AccessibleZone{
  def apply(field: Field, pos: Coord): AccessibleZone = {
    val accessibleCoord: Set[Coord] = BFS.doBFS(
      MoveNode.getFieldNodeAsAPlayerWhoCantPushBox(MoveNodeState(field, pos)),
      new BfsPlainQueue[MoveNode],
      (_: MoveNode) => false)
      .visitedNode
      .map(_.nodeState.pos)
    val accessibleBoxes: Set[Move] = accessibleCoord.flatMap{coord =>
      coord.adjacentSq.flatMap{adjCoord =>
        if(field(adjCoord).isABox)
          Some(coord.moveToGoToCoord(adjCoord))
        else None
      }
    }
    AccessibleZone(accessibleCoord, accessibleBoxes)
  }
}
case class AccessibleZone(accessibleCoord: Set[Coord], potentialPushBoxes: Set[Move])


