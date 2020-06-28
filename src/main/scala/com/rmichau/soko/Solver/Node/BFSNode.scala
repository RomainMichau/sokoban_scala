package com.rmichau.soko.Solver.Node

trait BFSNode[U <: BFSNode[U]] {
  def getConnectedNode: Set[U]
  val incomingEdge: Option[BFSIncomingEdge[U]]
  final val parentNode: Option[U] = incomingEdge.map(_.parentNode)
  def getPathToNode: Vector[U] = {
    parentNode match {
      case Some(parentNode) => parentNode.getPathToNode :+ parentNode
      case None => Vector()
    }
  }
  // Draw node on console (use to debug)
  def draw(): Unit
  def details: String
}

trait BFSIncomingEdge[U <: BFSNode[U]] {
  val parentNode: U
}
