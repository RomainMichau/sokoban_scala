
import java.net.URI

import com.rmichau.soko.GUI.{LevelPicker, SokoGui}
import com.rmichau.soko.Maze.{Coord, Maze, Move}
import com.rmichau.soko.Solver.Node.PushBoxNode
import com.rmichau.soko.Solver.{AccessibleZone, BFS, MazeSolver, SolverHelper}
import scalafx.application.JFXApp

object SokobanSolverLauncher {
  def main(args: Array[String]): Unit = {
    new SokobanSolver(args.headOption).main(Array())
  }
}


class SokobanSolver(levelFromArgument: Option[String]) extends JFXApp {



  val levelPicker: LevelPicker.type = LevelPicker

  val lvl: URI = if (levelFromArgument.isDefined) {
    new URI(levelFromArgument.get)
  } else {
    levelPicker.pickLevel
  }

  val maze = new Maze(lvl)
  val res: BFS.BFSResult[PushBoxNode] = new MazeSolver(maze).solveMaze()
  val gui = new SokoGui(maze)
  gui.stage()
  gui.drawMove(res.finalNode.get.toDirs)
}

