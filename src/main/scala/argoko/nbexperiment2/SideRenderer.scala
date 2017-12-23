package argoko.nbexperiment2

import java.awt.{Color, Component, Graphics, Graphics2D}
import javax.swing.{JLabel, JTable, SwingConstants}
import javax.swing.table.DefaultTableCellRenderer

class SideRenderer extends DefaultTableCellRenderer {
  override def getTableCellRendererComponent(table: JTable, value: scala.Any,
                                             isSelected: Boolean, hasFocus: Boolean,
                                             row: Int, column: Int): Component = {
    val ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).asInstanceOf[JLabel]
    val x = value.asInstanceOf[String]
    if ( x.equals("BUY")) {
      ret.setBackground(Color.GREEN)
      ret.setForeground(Color.BLACK)
    } else {
      ret.setBackground(Color.RED)
      ret.setForeground(Color.BLACK)
    }
    ret.setHorizontalAlignment(SwingConstants.CENTER)
    repaint()
    ret
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setPaint(Color.BLACK)
    g2.drawLine(0,0,10,10)
  }
}
