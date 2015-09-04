package threeStoners

import java.util.regex.Pattern
import scala.util.matching.Regex
import scala.actors.Actor

abstract class SmokingSupply {
  override def toString(): String = getClass.getSimpleName.replaceAllLiterally("$", "")
}
object Weed extends SmokingSupply
object Paper extends SmokingSupply
object Matches extends SmokingSupply

case class Message(from: SmokingSupply, to: SmokingSupply, message: String)

class Stoner(val supply: SmokingSupply) extends Actor {

  val stonerId = supply
  val stonerMessageHander = new StonerMessageHandler(this)
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
      case "requestSupply" => stonerMessageHander.requestSupply(message)
      case "takeSupply" => stonerMessageHander.takeSupply(message)
      case hitJointPattern(tokes) => stonerMessageHander.hitJoint(tokes.toInt, message)
      case "yourTurnToRoll" => stonerMessageHander.yourTurnToRoll(message)
      case "roll" => stonerMessageHander.roll(message)
      case unknownMessage => {
        println("ignoring message " + unknownMessage + " from: " + message.from + " to: " + message.to)
      }
    }
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