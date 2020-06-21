package com.rmichau.soko.Maze

import java.net.URI

import com.rmichau.soko.Solver.Deadlocks

import scala.io.Source


object Maze {
  private var nbLig = 0
  private var nbCol = 0

  def getNbLig: Int = nbLig

  def getNbCol: Int = nbCol
}

class Maze(filePath: URI) {
  // if you use intelliJ you need to configure the resource file in the project struct
  // see: https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000168244/comments/115000201030
  private var currentGameState = loadLevelFromFile(filePath)

  def field: Field = currentGameState.field

  private def posPlayer: Coord = currentGameState.playerPos

  private def boxes: Set[Coord] = currentGameState.posBoxes

  private def nbCol: Int = Maze.getNbCol

  private def nbLig: Int = Maze.getNbLig

  val goals: Set[Coord] = field.goals
  detectStaticDeadlocks()
  drawField()

  def getGameState: GameState = currentGameState

  def movePlayer(direction: Direction): Boolean = {
    val dest = posPlayer.getCoordAfterMove(direction)
    if (dest.isInField) {
      val destSq = field(dest)
      if (destSq.isWalkable) {
        this.currentGameState.playerPos = dest
        //this.drawField()
        hasWon
      }
      else if (destSq.isABox) {
        if (this.pushBox(dest, direction)) {
          this.currentGameState.playerPos = dest
          this.drawField()
        }
        hasWon
      }
      hasWon
    }
    hasWon
  }

  def changeGameState(gameState: GameState): Unit = {
    this.currentGameState = gameState
  }

  def reinitGame(): Unit = {
    this.currentGameState = loadLevelFromFile(filePath)
    this.detectStaticDeadlocks()
  }

  override def toString: String = {
    var res =""
    for (lig <- 0 until this.nbLig) {
      for (col <- 0 until this.nbCol) {
        val sq = this.field(Coord(lig, col))
        if (Coord(lig, col) != posPlayer)
          res+=sq.sym
        else res+=5
      }
      res+="\n"
    }
    res
  }

  private def detectStaticDeadlocks(): Unit = {
    println("=====DETECTING STATIC DEADLOCKS======")
    Deadlocks.detectStaticDeadLocks(field).foreach(sq => field += Square.deadSquare(sq.coord))
    println("=====DETECTING STATIC DEADLOCKS DONE======")

  }

  private def hasWon = {
     this.goals == this.boxes
  }

  private def pushBox(boxCoord: Coord, direction: Direction): Boolean = {
    canPushBox(boxCoord, direction).exists { dest =>
      field.pushBox(boxCoord, direction)
      true
    }
  }

  private def canPushBox(boxCoord: Coord, direction: Direction): Option[Coord] = {
    val dest = boxCoord.getCoordAfterMove(direction)
    val destsq = field(dest)
    val sq = field(boxCoord)
    if (sq.isABox && dest.isInField && destsq.isWalkable)
      Some(dest)
    else
      None
  }

  private def loadLevelFromFile(filePath: URI): GameState = {
    println(s"loading level from file $filePath")
    val source =
      try{
        Source.fromFile(filePath)
      } catch {
        case e: Exception => throw new Exception(s"lvl '${filePath.toString}' does not exist: $e")
      }

    val fileLines = source.getLines().toList
    source.close()
    Maze.nbCol = fileLines.map(_.length).max
    Maze.nbLig = fileLines.length
    var pos = Coord(0, 0)
    val fieldArray: Array[Array[Square]] = fileLines.zipWithIndex.map { case (li, idxli) =>
      li.toCharArray.zipWithIndex.map { case (sq, idxCol) =>
        if (sq.asDigit == 5) {
          pos = Coord(idxli, idxCol)
          Square(SquareType.Ground, pos)
        }
        else {
          Square(SquareType(sq.asDigit), Coord(idxli, idxCol))
        }
      } ++ (li.length until Maze.nbCol ).map(col => Square.wall(Coord(idxli, col)))
    }.toArray

    val fieldMap: Map[Coord, Square] =
    fieldArray.zipWithIndex.flatMap{ case (li, idxli) =>
      li.zipWithIndex.map { case (sq, idxCol) => Coord(idxli, idxCol) -> sq }
    }.toMap

    GameState(new Field(fieldMap), pos)
  }

  private def drawField(): Unit = {

    def getColor(square: Square): String = {
      square.sqType match {
        case SquareType.Box | SquareType.BoxPlaced => Console.BLUE
        case SquareType.Wall => Console.MAGENTA
        case SquareType.Goal => Console.GREEN
        case SquareType.Deadlock => Console.RED
        case SquareType.Ground => Console.WHITE
      }
    }

    // Print col index
    print("   ")
    (0 until this.nbCol).foreach { nb =>
      print(s"$nb ")
      if (nb < 10) print(" ")
    }
    println()

    for (lig <- 0 until this.nbLig) {
      print(lig + "  ")
      for (col <- 0 until this.nbCol) {
        val sq = this.field(Coord(lig, col))
        if (Coord(lig, col) != posPlayer)
          print(getColor(sq) + sq.sym + "  ")
        else print(Console.YELLOW + 5 + "  ")
      }
      println()
    }
  }
}

case class GameState(var field: Field, var playerPos: Coord){
  def posBoxes: Set[Coord] = field.getBoxes
}






