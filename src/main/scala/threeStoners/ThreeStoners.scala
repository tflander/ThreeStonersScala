package threeStoners

object ThreeStoners extends App {

  val circleOrder = Seq(Paper, Weed, Matches)
  val stoners = for (supply <- circleOrder) yield Stoner(supply)

  for (stoner <- stoners) {
    stoner.stoners = stoners
  }

  val stonerToRoll = stoners.head
  
  val firstMessage = new Message(from=null, to=stonerToRoll.stonerId, "yourTurnToRoll")

  for (smoker <- stoners) {
    smoker.start()
  }

  stonerToRoll ! firstMessage

  Thread.sleep(100)

  for (smoker <- stoners) {
    smoker ! "EXIT"
  }

  println("finished")
}