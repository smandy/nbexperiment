package argoko.nbexperiment2

import java.util
import java.util.concurrent.{Executors, TimeUnit}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import javax.swing.{SwingUtilities, UIManager, UnsupportedLookAndFeelException}

import argoko.{Coordinator, FrameExperiment}
import ca.odell.glazedlists.BasicEventList
import praxis.guilego.{PraxisSwingUtil, ScalaColumnInfo, ScalaTableModelUtil}

import scala.beans.BeanProperty
import scala.reflect.ClassTag
import scala.util.Random


case class RandomChoice[T]( xs_ : T*)(implicit ev : ClassTag[T]) {

  val xs = xs_.toArray

  val rand = new java.util.Random()

  def next() : T = {
    val idx = rand.nextInt().abs
    xs(idx % xs.length)
  }
}


object TradeBean {
  val sideChoice = RandomChoice("BUY", "SELL")
  val securityChoice = RandomChoice("AAPL", "BTC", "GLW", "CSCO", "IBM", "AMAT")

  def random( qty : Int) = {
    val side = sideChoice.next()
    val security = securityChoice.next()
    TradeBean(qty, side, security)
  }
}

case class TradeBean(@BeanProperty val qty: Int,
                     @BeanProperty val side : String,
                     @BeanProperty val security : String)

object MyNbexperiment {

  def main(args: Array[String]): Unit = {

    new MyNbexperiment()
  }

  val MIN_RATE = 1
  val MAX_RATE = 1000
  val DEFAULT_RATE = 1
}

class MyNbexperiment {

  import MyNbexperiment.{DEFAULT_RATE, MIN_RATE, MAX_RATE}

  val (eventList) = PraxisSwingUtil.invokeLaterAndWaitForResult {
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

    val columns = Array(
      ScalaColumnInfo("qty", "qty"),
      ScalaColumnInfo("side", "side", renderer = Some(new SideRenderer)),
      ScalaColumnInfo("security", "security")
    )
    val myEventList = new BasicEventList[TradeBean]()

    val running = new AtomicBoolean(false)
    val executor = Executors.newSingleThreadScheduledExecutor()
    val qty = new AtomicInteger()
    val frame = new FrameExperiment()

    {
      val r = frame.jsRate
      r.setMinimum(MIN_RATE)
      r.setMaximum(MAX_RATE)
      r.setValue(DEFAULT_RATE)
    }

    val rand = new java.util.Random()

    def addOrder() {
      if (rand.nextBoolean()) {
        myEventList.add(TradeBean.random(qty.get()))
      }
    }

    def removeOrder() {
      if (rand.nextBoolean() && myEventList.size() > 0) {
        myEventList.remove(0)
      }
    }

    def a2r(f: => Unit): Runnable = () => SwingUtilities.invokeLater(() => f)

    def doWork(): Unit = {
      qty.incrementAndGet()
      addOrder()
      removeOrder()
      val perSecond = frame.jsRate.getValue().max(MIN_RATE).min(MAX_RATE)
      val millis = (1000.0 / perSecond).toLong
      //println(s"Millis is $millis perSecond=$perSecond")
      if (running.get()) {
        executor.schedule(a2r {
          doWork
        }, millis, TimeUnit.MILLISECONDS)
      }
    }

    val coordinator = new Coordinator {
      override def onButtonOne(): Unit = {
        running.set(true)
        SwingUtilities.invokeLater(() => doWork())
      }

      override def onButtonTwo(): Unit = {
        running.set(false)
      }
    }

    frame.setCoordinator(coordinator)

    ScalaTableModelUtil.configureJTable(frame.tblOne, columns, myEventList)
    frame.setVisible(true)
    (myEventList)
  }
}
