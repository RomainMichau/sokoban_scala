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
    var nPos: Coord = accessibleCoord.head
    val accessibleBoxes: Set[Move] = field.boxes.flatMap{coord =>
      if(coord.col < nPos.col) {
        nPos = coord
      } else if(coord.col == nPos.col && coord.lig < nPos.lig) {
        nPos = coord
      }
      coord.adjacentSq.filter(accessibleCoord(_)).map(adjCoord => adjCoord.moveToGoToCoord(coord))
      }
    AccessibleZone(accessibleCoord, accessibleBoxes, nPos)
  }
}
case class AccessibleZone(accessibleCoord: Set[Coord], potentialPushBoxes: Set[Move], nPos: Coord)


