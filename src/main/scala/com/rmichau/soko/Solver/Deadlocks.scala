package com.rmichau.soko.Solver

import com.rmichau.soko.Maze.{Field, Square, SquareType}

import scala.collection.mutable

object Deadlocks {
  def detectStaticDeadLocks(field: Field): Iterable[Square] = {
    field.getAllSquares
      .filter(sq => sq.sqType == SquareType.Ground)
      .filter{sq =>
        BFS.doBFS(FieldNode.getFieldNodeAsABoxInAnEmptyField(field, sq.coord), new BfsPlainQueue[BFSNode]).isEmpty
      }

  }
}
