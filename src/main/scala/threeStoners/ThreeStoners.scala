package threeStoners

import scala.collection.mutable.Queue

object ThreeStoners extends App {

  val circleOrder = Seq(Paper, Weed, Matches)
  val stoners = for (stoner <- circleOrder) yield Stoner(stoner, circleOrder)

  for (smoker <- stoners) {
    smoker.stoners = stoners
  }

  val firstMessage = new Message(null, Paper, "yourTurnToRoll")

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