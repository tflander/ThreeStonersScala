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

case class Message(from: String, to: String, message: String)

class Stoner(val stonerId: String, val supply: SmokingSupply, out: java.io.ByteArrayOutputStream = null) extends Actor {

  val stonerMessageHander = new StonerMessageHandler(this, out)
  var supplyCount = 0
  
  val suppliesNeeded = supply match {
    case Paper => Set(Weed, Matches)
    case Weed => Set(Paper, Matches)
    case Matches => Set(Weed, Paper)
  }
  
  var hippyCircle: HippyCircle = _

  lazy val nextStoner = {
    val stoners = hippyCircle.stoners
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
      case "EXIT" => 
    }
  }
}