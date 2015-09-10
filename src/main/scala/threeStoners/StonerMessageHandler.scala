package threeStoners

class StonerMessageHandler(stoner: Stoner, out: java.io.ByteArrayOutputStream = null) {
  
  lazy val stoners = stoner.stoners
  
  def requestSupply(message: Message) {
    val supply = stoners.filter(_.stonerId == message.to).head.supply.toString.toLowerCase
    log(message.from + " requested " + supply + " from " + message.to)
    sendMessage(to = message.from, "takeSupply")
  }

  def takeSupply(message: Message) {
    val supply = stoners.filter(_.stonerId == message.from).head.supply.toString.toLowerCase
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
    stoners.foreach(otherStoner => {
      if (otherStoner.stonerId != stoner.stonerId) {
        sendMessage(otherStoner.stonerId, "requestSupply")
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
    stoners.filter(_.stonerId == to).head ! Message(from = stoner.stonerId, to, message)
  }

  def log(msg: String) = {
    if(out == null) {
    	println(Thread.currentThread().getName() + ": " + msg)      
    } else {
      out.write(msg.getBytes)
      out.write('\n')
    }
  }
}