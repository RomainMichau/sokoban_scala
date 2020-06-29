package com.rmichau.soko.Solver

import java.io.{File, PrintWriter}
import java.util.Calendar

import com.rmichau.soko.Solver.Node.BFSNode
import com.rmichau.soko.util.Util

import scala.collection.{immutable, mutable}

object BFS {

  case class BFSResult[U <: BFSNode[U]](
      finalNode: Option[U],
      visitedNode: Set[U],
      timeToFindRes: Long,
      nodeToFindRes: Int
  ) {
    override def toString: String =
      s"visited Nodes: $visitedNode; time: $timeToFindRes ms"
  }

  /**
    * Do a bfs to fin a BFSNode marked as goal from the node sent in parameters
    *
    * @param node Begining node of the BFS
    * @return return the Fist node marked as a bfs goal. Path to this node can be get with node.getPathToNode
    *         If no goalNode is reachable None in returned
    */
  def doBFS[U <: BFSNode[U]](
      node: U,
      queue: BfsQueue[U],
      isGoalNode: U => Boolean,
      disp: Boolean = false,
      debug: Boolean = false,
      bfsDecription: String = ""
  ): BFSResult[U] = {
    if (queue.nonEmpty) {
      throw new Exception("queue must be empty")
    }
    val chrono = Util.Chrono()
    queue.enqueue(node)
    var visitedNode: Set[U] = Set(node)
    var won = isGoalNode(node)
    var finalNode: Option[U] = if (won) {
      Some(node)
    } else {
      None
    }
    while (queue.nonEmpty && !won) {
      val cuNode = queue.dequeue()
      if (debug) {
        println(s"=====Current Node: ${cuNode.details}")
      }
      cuNode.getConnectedNode.foreach { node =>
        if (debug) {
          println(s"neigb Node: ${node.details}")
        }
        if (!won) {
          if (isGoalNode(node)) {
            won = true
            finalNode = Some(node)
          } else if (!visitedNode(node)) {
            queue.enqueue(node)
            visitedNode = visitedNode + node
          }
        }
      }
    }
    val resTxt = s"level: $bfsDecription" +
      s"solution found $won \n" +
      "BFS Elapsed time: " + (chrono.currentTime) + "ms\n" +
      s"Nove visited: ${visitedNode.size}"
    if (disp) {
      print(resTxt)
    }
    if (disp) {
      writeInFile(resTxt)
    }
    BFSResult(finalNode, visitedNode, chrono.currentTime, visitedNode.size)

  }

  private def writeInFile(resTxt: String): Unit = {
    val file = new File("previous_res")
    val line = if (file.exists()) {
      val source = scala.io.Source.fromFile(file)
      val value = source.mkString
      source.close()
      Some(value)
    } else {
      None
    }

    val writer = new PrintWriter(new File("previous_res"))
    writer.write(
      Calendar
        .getInstance()
        .getTime + "\n" + resTxt + "\n=============== \n" + line.getOrElse("")
    )
    writer.close()
  }
}

trait BfsQueue[U] {
  def enqueue(v: U*): Unit

  def dequeue(): U

  def nonEmpty: Boolean
}

class BfsPlainQueue[U] extends mutable.Queue[U] with BfsQueue[U]

class BfsPriorityQueue[U](ord: Ordering[U]) extends BfsQueue[U] {
  val queue: mutable.PriorityQueue[U] = mutable.PriorityQueue[U]()(ord)

  override def enqueue(v: U*): Unit = v.foreach(queue.enqueue(_))

  override def dequeue(): U = queue.dequeue()

  override def nonEmpty: Boolean = queue.nonEmpty

}
