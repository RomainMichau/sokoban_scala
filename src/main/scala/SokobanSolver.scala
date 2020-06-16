import java.awt.BorderLayout

import com.rmichau.soko.GUI.LevelPicker
import com.rmichau.soko.Maze.Maze
import scalafx.application.JFXApp

object SokobanSolver extends JFXApp{
    val levelPicker = new LevelPicker

    val lvl = levelPicker.pickLevel

    val maze = new Maze(lvl)
    val gui = new SokoGui(maze)
    gui.stage


}