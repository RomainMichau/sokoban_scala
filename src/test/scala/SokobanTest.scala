import java.io.File

import com.rmichau.soko.GUI.LevelPicker
import com.rmichau.soko.Maze.Maze
import com.rmichau.soko.Solver.Node.PushBoxNode
import com.rmichau.soko.Solver.{BFS, MazeSolver}
import com.rmichau.soko.util.Util
import org.scalatest.FunSuite

class SokobanTest extends FunSuite {
 val levels: Array[File] = Util.recursiveListFiles(new File("test/levels")).filter(_.isFile)
 test("level file reader"){
  assert(levels.toSeq.toString() == "WrappedArray(test/levels/medium/medium_3.dat, test/levels/medium/medium_2.dat, test/levels/medium/medium_1.dat," +
    " test/levels/test/test_1.dat, test/levels/test/test_4.dat, test/levels/test/test_2.dat, test/levels/easy/easy_1.dat, test/levels/easy/easy_2.dat)")
 }
/*
 test("test maze loading + static deadlock detection"){
  val mazee1 = new Maze(levels.filter(_.toURI.toString.contains("easy_1"))(0).toURI)
  assert(mazee1.toString=="111111\n194111\n100111\n135091\n100291\n199111\n111111\n111111\n")
 }
 /**/
 val solvedMazeE1: BFS.BFSResult[PushBoxNode] = mazeSolver.solveMaze()

 test("easy1 should be solved"){
  assert(solvedMazeE1.finalNode.isDefined)
  assert(solvedMazeE1.finalNode.get.toDirs.size > 1)
 }*/

 val mazem1 = new Maze(levels.filter(_.toURI.toString.contains("medium_1"))(0).toURI)
 val mazeSolver: MazeSolver = new MazeSolver(mazem1)
 val solvedMazeM1: BFS.BFSResult[PushBoxNode] = mazeSolver.solveMaze()

 test("medium 1 should be solved"){
  assert(solvedMazeM1.finalNode.isDefined)
  assert(solvedMazeM1.finalNode.get.toDirs.size > 1)
 }
}

