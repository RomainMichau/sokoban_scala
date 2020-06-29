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
  assert(levels.toSeq.toString() == "WrappedArray(test/levels/test/test_1.dat, test/levels/test/test_4.dat, test/levels/test/test_2.dat" +
    ", test/levels/easy/easy_1.dat, test/levels/easy/easy_2.dat)")
 }
 val maze = new Maze(levels.filter(_.toURI.toString.contains("easy_1"))(0).toURI)
 test("test maze loading + static deadlock detection"){
  assert(maze.toString=="111111\n194111\n100111\n135091\n100291\n199111\n111111\n111111\n")
 }
 val mazeSolver: MazeSolver = new MazeSolver(maze)
 val solvedMaze: BFS.BFSResult[PushBoxNode] = mazeSolver.solveMaze()

 test("easy1 should be solved"){
  assert(solvedMaze.finalNode.isDefined)
  assert(solvedMaze.finalNode.get.toDirs.size > 1)
 }
}

