package threeStoners

class StonerMessageHandler(stoner: Stoner, out: java.io.ByteArrayOutputStream = null) {

  lazy val hippyCircle = stoner.hippyCircle
  
  def requestSupply(message: Message) {
    val supply = hippyCircle.getStonerById(message.to).supply.toString.toLowerCase
    log(message.from + " requested " + supply + " from " + message.to)
    sendMessage(to = message.from, "takeSupply")
  }

  def takeSupply(message: Message) {
    val supply = hippyCircle.getStonerById(message.from).supply.toString.toLowerCase
    log(message.to + " takes " + supply + " from " + message.from)
    stoner.supplyCount += 1
    if (stoner.supplyCount == 2) {
      sendMessage(to = stoner.stonerId, "roll")
    }
  }

  def hitJoint(remainingTokes: Int, message: Message) {
    if (message.from == stoner.stonerId) {
      log(message.to + " takes a toke")
    } else {
      log(message.to + " takes joint with " + remainingTokes + " hits left from " + message.from + " and tokes")
    }
    if (remainingTokes == 1) {
      sendMessage(to = stoner.stonerId, "yourTurnToRoll")
    } else {
      sendMessage(to = stoner.nextStoner, "hitJoint" + (remainingTokes - 1))
    }
  }

  def yourTurnToRoll(message: Message) {
    log(message.to + " needs to roll a joint")
    stoner.supplyCount = 0

    def requestSupplyFrom(dudes: Seq[Stoner]) {
      import scala.util.Random
      val randomDude = dudes(Random.nextInt(dudes.size))
      sendMessage(randomDude.stonerId, "requestSupply")
    }

    stoner.suppliesNeeded foreach (supply => {
      supply match {
        case Weed => requestSupplyFrom(hippyCircle.weedGuys)
        case Paper => requestSupplyFrom(hippyCircle.paperGuys)
        case Matches => requestSupplyFrom(hippyCircle.matchesGuys)
      }
    })

  }

  def roll(message: Message) {
    import scala.util.Random
    val numTokes = Random.nextInt(5) + 7
    log(message.to + " rolls a " + numTokes + " hit joint and lights it")
    sendMessage(to = stoner.stonerId, "hitJoint" + numTokes)
  }

  def sendMessage(to: String, message: String) = {
    hippyCircle.getStonerById(to) ! Message(from = stoner.stonerId, to, message)
  }

  def log(msg: String) = {
    if (out == null) {
      println(Thread.currentThread().getName() + ": " + msg)
    } else {
      out.write(msg.getBytes)
      out.write('\n')
    }
  }
}