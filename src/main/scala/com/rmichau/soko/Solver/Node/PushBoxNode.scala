package com.rmichau.soko.Solver.Node

import com.rmichau.soko.Maze.{Coord, Field, GameState}


/*
case class PushBoxNode(state: PushBoxNodeState,
                     parentNode: Option[PushBoxNode]) extends BFSNode[PushBoxNode]{

  //override def getConnectedNode: Set[PushBoxNode] = ???

  override def isGoalNode: Boolean = state.field.goals == state.field.getBoxes
}

case class PushBoxNodeState(field: Field, pos: Coord) {
  def this(gameState: GameState) = this(gameState.field, gameState.playerPos)
}*/