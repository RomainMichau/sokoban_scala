package com.rmichau.soko.Solver.Node

trait BFSNode[U <: BFSNode[U]] {
  def getConnectedNode: Set[U]
  val parentNode: Option[U]
  def getPathToNode: Vector[U] = {
    parentNode match {
      case Some(parentNode) => parentNode.getPathToNode :+ parentNode
      case None => Vector()
    }
  }
  // Draw node on console (use to debug)
  def draw(): Unit
}

trait BFSEdge[U <: BFSNode[U]] {
  val node1: U
  val node2: U
}
