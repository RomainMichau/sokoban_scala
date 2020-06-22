
import java.net.URI

import com.rmichau.soko.GUI.{LevelPicker, SokoGui}
import com.rmichau.soko.Maze.Maze
import com.rmichau.soko.Solver.{MazeSolver, SolverHelper}
import scalafx.application.JFXApp

object SokobanSolverLauncher {
  def main(args: Array[String]): Unit = {
    new SokobanSolver(args.headOption).main(Array())
  }
}


class SokobanSolver(levelFromArgument: Option[String]) extends JFXApp {



  val levelPicker: LevelPicker.type = LevelPicker

  val lvl: URI = if (levelFromArgument.isDefined)
    new URI(levelFromArgument.get)
  else
    levelPicker.pickLevel

  val maze = new Maze(lvl)
  val map = SolverHelper.getDistMap(maze.field)
  val res = new MazeSolver(maze).solveMaze()
  val gui = new SokoGui(maze)
  gui.stage()
  gui.drawMove(res.finalNode.get)
}

