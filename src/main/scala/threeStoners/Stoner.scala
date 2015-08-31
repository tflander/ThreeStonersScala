package threeStoners

import scala.collection.mutable.Queue
import java.util.regex.Pattern
import scala.util.matching.Regex
import scala.util.Random

abstract class SmokingSupply {
  override def toString(): String = getClass.getSimpleName.replaceAllLiterally("$", "")
}
object Weed extends SmokingSupply
object Paper extends SmokingSupply
object Matches extends SmokingSupply

case class Stoner(supply: SmokingSupply, messageQueue: Queue[Message], circleOrder: Seq[SmokingSupply]) extends Runnable {

  val stonerId = supply
  var supplyCount = 0

  val nextStoner = {
    val stonerCount = circleOrder.size
    val me = circleOrder.indexOf(stonerId)
    me + 1 match {
      case `stonerCount` => circleOrder(0)
      case nextStoner => circleOrder(me + 1)
    }
  }

  def processMessage(message: Message) = {
    require(message.to == stonerId)
    val hitJointPattern = "hitJoint(\\d+)".r

    message.message match {
      case "requestSupply" => {
        println(message.from + " guy requested supply from " + message.to + " guy")
        sendMessage(to = message.from, "takeSupply")
      }
      case "takeSupply" => {
        println(message.to + " guy takes supply from " + message.from + " guy")
        supplyCount += 1
        if (supplyCount == 2) {
          sendMessage(to = stonerId, "roll")
        }
      }
      case hitJointPattern(tokes) => {
        if (message.from == stonerId) {
          println(message.to + " guy takes a toke")
        } else {
          println(message.to + " guy takes joint with " + tokes + " hits left from " + message.from + " guy and tokes")
        }
        val remainingTokes = tokes.toInt
        if (remainingTokes == 1) {
          sendMessage(to = stonerId, "yourTurnToRoll")
        } else {
          sendMessage(to = nextStoner, "hitJoint" + (remainingTokes - 1))
        }
      }
      case "yourTurnToRoll" => {
        println(message.to + " guy needs to roll a joint")
        supplyCount = 0
        circleOrder.foreach(stoner => {
          if (stoner != stonerId) {
            sendMessage(stoner, "requestSupply")
          }
        })
      }
      case "roll" => {
        val numTokes = Random.nextInt(5) + 7
        println(message.to + " guy rolls a " + numTokes + " hit joint and lights it")
        sendMessage(to = stonerId, "hitJoint" + numTokes)
      }
      case unknownMessage => {
        println("ignoring message " + unknownMessage + " from: " + message.from + " to: " + message.to)
      }
    }
  }

  def sendMessage(to: SmokingSupply, message: String) = {
    messageQueue += Message(from = stonerId, to, message)
  }

  def handleTransitions() = {
    messageQueue.synchronized {
      messageQueue.get(0) match {
        case Some(message) => {
          if (message.to == stonerId) {
            processMessage(messageQueue.dequeue)
          }
        }
        case None =>
      }
    }
  }

  override def run() {
    while (!Thread.interrupted()) {
      try {
        handleTransitions();
      } catch {
        case e: InterruptedException => return
        case t: Throwable => {
          t.printStackTrace();
          System.exit(1);
        }
      }
    }
  }

}