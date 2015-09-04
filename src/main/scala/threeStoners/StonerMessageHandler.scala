package threeStoners

class StonerMessageHandler(stoner: Stoner) {
  
  lazy val stoners = stoner.stoners
  
  def requestSupply(message: Message) {
    println(message.from + " guy requested supply from " + message.to + " guy")
    sendMessage(to = message.from, "takeSupply")
  }

  def takeSupply(message: Message) {
    println(message.to + " guy takes supply from " + message.from + " guy")
    stoner.supplyCount += 1
    if (stoner.supplyCount == 2) {
      sendMessage(to = stoner.stonerId, "roll")
    }
  }

  def hitJoint(remainingTokes: Int, message: Message) {
    if (message.from == stoner.stonerId) {
      println(message.to + " guy takes a toke")
    } else {
      println(message.to + " guy takes joint with " + remainingTokes + " hits left from " + message.from + " guy and tokes")
    }
    if (remainingTokes == 1) {
      sendMessage(to = stoner.stonerId, "yourTurnToRoll")
    } else {
      sendMessage(to = stoner.nextStoner, "hitJoint" + (remainingTokes - 1))
    }
  }

  def yourTurnToRoll(message: Message) {
    println(message.to + " guy needs to roll a joint")
    stoner.supplyCount = 0
    stoners.foreach(otherStoner => {
      if (otherStoner.stonerId != stoner.stonerId) {
        sendMessage(stoner.stonerId, "requestSupply")
      }
    })
  }

  def roll(message: Message) {
    import scala.util.Random
    val numTokes = Random.nextInt(5) + 7
    println(message.to + " guy rolls a " + numTokes + " hit joint and lights it")
    sendMessage(to = stoner.stonerId, "hitJoint" + numTokes)
  }

  def sendMessage(to: SmokingSupply, message: String) = {
    stoners.filter(_.supply == to).head ! Message(from = stoner.stonerId, to, message)
  }

}