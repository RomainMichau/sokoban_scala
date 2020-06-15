import java.awt.BorderLayout

import com.rmichau.soko.Maze.Maze
import scalafx.application.JFXApp

object SokobanSolver extends JFXApp{
    val maze = new Maze()
    val gui = new SokoGui(maze)
    gui.stage
}