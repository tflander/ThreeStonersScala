package threeStoners

import scala.collection.mutable.Queue

object ThreeStoners extends App {

  val circleOrder = Seq(Paper, Weed, Matches)
  val messageQueue = new Queue[Message]
  val stoners = for (stoner <- circleOrder) yield Stoner(stoner, messageQueue, circleOrder)

		messageQueue += new Message(null, Matches, "yourTurnToRoll");

		for (smoker <- stoners) {
		  smoker.start()
		}
		
		Thread.sleep(100)
		
		System.exit(0)  // TODO:  BAD!!!

		println("finished")
}