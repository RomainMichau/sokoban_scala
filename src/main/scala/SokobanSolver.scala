
import java.net.URI

import com.rmichau.soko.GUI.LevelPicker
import com.rmichau.soko.Maze.Maze
import scalafx.application.JFXApp

object SokobanSolverLauncher {
  def main(args: Array[String]): Unit = {
    new SokobanSolver(args.lift(0)).main(Array())
  }
}


class SokobanSolver(levelFromArgument: Option[String]) extends JFXApp {
  val levelPicker = new LevelPicker

  val lvl: URI = if (levelFromArgument.isDefined)
    new URI(levelFromArgument.get)
  else
    levelPicker.pickLevel

  val maze = new Maze(lvl)
  val gui = new SokoGui(maze)
  gui.stage
}

