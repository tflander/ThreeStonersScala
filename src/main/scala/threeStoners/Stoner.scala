package threeStoners

import java.util.regex.Pattern
import scala.util.matching.Regex
import scala.util.Random
import scala.actors.Actor

abstract class SmokingSupply {
  override def toString(): String = getClass.getSimpleName.replaceAllLiterally("$", "")
}
object Weed extends SmokingSupply
object Paper extends SmokingSupply
object Matches extends SmokingSupply

case class Message(from: SmokingSupply, to: SmokingSupply, message: String)

case class Stoner(supply: SmokingSupply) extends Actor {

  val stonerId = supply
  var supplyCount = 0

  var stoners: Seq[Stoner] = _
  
  lazy val nextStoner = {
    val stonerCount = stoners.size
    val me = stoners.indexOf(this)
    me + 1 match {
      case `stonerCount` => stoners(0).stonerId
      case nextStoner => stoners(me + 1).stonerId
    }
  }

  def processMessage(message: Message) = {
    require(message.to == stonerId, "invalid message:to.  Got " + message.to + ", expected " + stonerId)
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
        stoners.foreach(stoner => {
          if (stoner.stonerId != stonerId) {
            sendMessage(stoner.stonerId, "requestSupply")
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
    stoners.filter(_.supply == to).head ! Message(from = stonerId, to, message)
  }

  override def act() {
    
    react {
      case message: Message => {
        processMessage(message)
        act()
      }
      case "EXIT" => println(stonerId + " guy exiting")
    }    
  }
}