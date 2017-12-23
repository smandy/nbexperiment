package argoko.nbexperiment2

import javax.swing.{UIManager, UnsupportedLookAndFeelException}

import argoko.FrameExperiment

object MyNbexperiment {

  def main(args: Array[String]): Unit = {
    java.awt.EventQueue.invokeLater(() => {
      try
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
      catch {
        case e: ClassNotFoundException =>
          e.printStackTrace()
        case e: InstantiationException =>
          e.printStackTrace()
        case e: IllegalAccessException =>
          e.printStackTrace()
        case e: UnsupportedLookAndFeelException =>
          e.printStackTrace()
      }
      new FrameExperiment().setVisible(true)
    })
  }
}
