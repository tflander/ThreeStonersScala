package threeStoners

import scala.collection.mutable.Queue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ThreeStoners extends App {

  val circleOrder = Seq(Paper, Weed, Matches)
  val messageQueue = new Queue[Message]
  val stoners = for (stoner <- circleOrder) yield Stoner(stoner, messageQueue, circleOrder)

  // TODO: Scala way?
  val executor = Executors.newFixedThreadPool(10);
		messageQueue += new Message(null, Matches, "yourTurnToRoll");

		for (smoker <- stoners) {
			executor.submit(smoker);
		}

		executor.awaitTermination(100, TimeUnit.MILLISECONDS);
		executor.shutdownNow();
		println("finished")
}