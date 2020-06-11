package com.rmichau.soko.GUI

import java.awt.BorderLayout

import javax.swing.{JFrame, JScrollPane, JTextArea}

import scala.swing.Dimension
class SokoGui{

val textArea = new JTextArea
textArea.setText("Hello, Swing world")
val scrollPane = new JScrollPane(textArea)

val frame = new JFrame("Hello, Swing")
frame.getContentPane.add(scrollPane, BorderLayout.CENTER)
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
frame.setSize(new Dimension(600, 400))
frame.setLocationRelativeTo(null)
frame.setVisible(true)}
