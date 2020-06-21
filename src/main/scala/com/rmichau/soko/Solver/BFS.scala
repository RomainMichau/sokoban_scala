package com.rmichau.soko.Solver


import com.rmichau.soko.Solver.Node.BFSNode

import scala.collection.{immutable, mutable}


object BFS {

case class BFSResult[U <: BFSNode[U]](finalNode: Option[U], visitedNode: Set[U])

  /**
   * Do a bfs to fin a BFSNode marked as goal from the node sent in parameters
   * @param node Begining node of the BFS
   * @return return the Fist node marked as a bfs goal. Path to this node can be get with node.getPathToNode
   *         If no goalNode is reachable None in returned
   */
  def doBFS[U <: BFSNode[U]](node: U, queue: BfsQueue[U], isGoalNode: U => Boolean):BFSResult[U] = {
    if(queue.nonEmpty)
      throw new Exception("queue must be empty")
    val t0 = System.currentTimeMillis()
    queue.enqueue(node)
    var visitedNode: Set[U] = Set(node)
    var won = false
    var finalNode: Option[U] = None
    while(queue.nonEmpty){
      val cuNode = queue.dequeue()
      cuNode.getConnectedNode.foreach{ node =>
        if(!won){
          if(isGoalNode(node)) {
            won = true
            finalNode = Some(node)
          } else if(!visitedNode(node)){
            queue.enqueue(node)
            visitedNode = visitedNode + node
          }
        }
      }
    }
    val t1 = System.currentTimeMillis()
    println("BFS Elapsed time: " + (t1 - t0) + "ms")
    BFSResult(finalNode, visitedNode)
  }
}


trait BfsQueue[U]{
  def enqueue(v: U*): Unit
  def dequeue(): U
  def nonEmpty: Boolean

}

class BfsPlainQueue[U] extends mutable.Queue[U] with BfsQueue[U]

class BfsPriorityQueue[U](ord: Ordering[U]) extends BfsQueue[U]{
  val queue: mutable.PriorityQueue[U] = mutable.PriorityQueue[U]()(ord)

  override def enqueue(v: U*): Unit =  v.foreach(queue.enqueue(_))

  override def dequeue(): U = queue.dequeue()

  override def nonEmpty: Boolean = queue.isEmpty
}


