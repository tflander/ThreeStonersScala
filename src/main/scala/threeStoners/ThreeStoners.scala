package threeStoners

import scala.collection.mutable.Queue

object ThreeStoners extends App {

  val circleOrder = Seq(Paper, Weed, Matches)
  val messageQueue = new Queue[Message]
  val stoners = for (stoner <- circleOrder) yield Stoner(stoner, messageQueue, circleOrder)

  		val firstMessage = new Message(null, Matches, "yourTurnToRoll") 
		messageQueue += firstMessage;

		for (smoker <- stoners) {
		  smoker.start()
		}
		
		stoners.head ! firstMessage
		
		Thread.sleep(100)
		
		for (smoker <- stoners) {
		  smoker ! "EXIT"
		}

		println("finished")
}