package com.rmichau.soko.Solver


import scala.collection.mutable


object BFS {



  /**
   * Do a bfs to fin a BFSNode marked as goal from the node sent in parameters
   * @param node Begining node of the BFS
   * @return return the Fist node marked as a bfs goal. Path to this node can be get with node.getPathToNode
   *         If no goalNode is reachable None in returned
   */
  def doBFS(node: BFSNode, queue: BfsQueue[BFSNode]):Option[BFSNode] = {
    if(queue.nonEmpty)
      throw new Exception("queue must be empty")
    queue.enqueue(node)
    val visitedNode: mutable.Set[BFSNode] = mutable.Set(node)
    var won = false
    var finalNode: Option[BFSNode] = None
    while(queue.nonEmpty){
      val cuNode = queue.dequeue()
      cuNode.getNeighbourNode.foreach{ node =>
        if(!won){
          if(node.isGoalNode) {
            won = true
            finalNode = Some(node)
          } else if(!visitedNode(node)){
            queue.enqueue(node)
            visitedNode += node
          }
        }
      }
    }
    finalNode
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


