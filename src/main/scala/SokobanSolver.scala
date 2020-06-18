
import java.net.URI

import com.rmichau.soko.GUI.LevelPicker
import com.rmichau.soko.Maze.Maze
import com.rmichau.soko.Solver.{BFS, FieldNode}
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


  val node = FieldNode.getFieldNodeAsABoxInAnEmptyField(maze.field, maze.getGameState().posBoxes.toList(0))

  val gui = new SokoGui(maze)
  gui.stage()
  val res = BFS.doBFS(node)
  print(res.getPathToNode)
  print(node)
}

