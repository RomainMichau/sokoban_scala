package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Coord, Field, Move}
import com.rmichau.soko.Solver.Node.{MoveNode, MoveNodeState}

object AccessibleZone {
  def apply(field: Field, pos: Coord): AccessibleZone = {
    var nPos: Coord = Coord(2000, 2000)
    val accessibleCoord: Set[Coord] = BFS
      .doBFS(
        MoveNode.getMoveNodeAsAPlayerWhoCantPushBox(MoveNodeState(field, pos)),
        new BfsPlainQueue[MoveNode],
        (_: MoveNode) => false
      )
      .visitedNode
      .map { node =>
        val coord = node.nodeState.pos
        if (coord.col < nPos.col) {
          nPos = coord
        } else if (coord.col == nPos.col && coord.lig < nPos.lig) {
          nPos = coord
        }
        coord
      }

    val accessibleBoxes: Set[Move] = field.boxes.flatMap { coord =>
      coord.adjacentSq
        .filter(accessibleCoord(_))
        .map(adjCoord => adjCoord.moveToGoToCoord(coord))
    }
    AccessibleZone(accessibleCoord, accessibleBoxes, nPos)
  }
}
case class AccessibleZone(
    accessibleCoord: Set[Coord],
    potentialPushBoxes: Set[Move],
    nPos: Coord
)
