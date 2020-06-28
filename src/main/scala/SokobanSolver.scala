
import java.net.URI

import com.rmichau.soko.GUI.{LevelPicker, SokoGui}
import com.rmichau.soko.Maze.{Coord, Maze, Move}
import com.rmichau.soko.Solver.Node.PushBoxNode
import com.rmichau.soko.Solver.{AccessibleZone, BFS, MazeSolver, SolverHelper}
import scalafx.application.JFXApp

object SokoGlobalValue  {
  var debugMode: Boolean = false
}

object SokobanSolverLauncher {
  def argumentParser(args: Array[String]): SokoArguments = {
    val level = args.find(_.contains("-level:")).map(_.replace("-level:", ""))
    val debug = args.exists(_.contains("-debug"))
    SokoArguments(level, debug)
  }
  def main(args: Array[String]): Unit = {
    val arguments = argumentParser(args)
    SokoGlobalValue.debugMode = arguments.debug
    new SokobanSolver(arguments.level).main(Array())
  }

  case class SokoArguments(level: Option[String], debug: Boolean)
}


class SokobanSolver(levelFromArgument: Option[String]) extends JFXApp {



  val levelPicker: LevelPicker.type = LevelPicker

  val lvl: URI = if (levelFromArgument.isDefined) {
    new URI(levelFromArgument.get)
  } else {
    levelPicker.pickLevel
  }

  val maze = new Maze(lvl)
 // val res: BFS.BFSResult[PushBoxNode] = new MazeSolver(maze).solveMaze()
  val gui = new SokoGui(maze)
  gui.stage()
 // gui.drawMove(res.finalNode.get.toDirs)
}

