package com.rmichau.soko.Solver


import scala.collection.mutable
import scala.util.hashing.MurmurHash3

object BFS {
  def doBFS(node: BFSNode) = {
    val queue = mutable.Queue(node)
    val visitedNode: mutable.Set[BFSNode] = mutable.Set(node)
    var won = false
    var finalNode: BFSNode = null
    while(queue.nonEmpty){
      val cuNode = queue.dequeue()
      cuNode.getNeighbourNode.foreach{ node =>
        if(node.isGoalNode) {
          won = true
          finalNode = node
        } else if(!visitedNode(node)){
          queue.enqueue(node)
          visitedNode += node
        }
      }
    }
    finalNode
  }
}


