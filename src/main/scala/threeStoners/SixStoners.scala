package threeStoners

object SixStoners extends App {

  val stoners = Seq(
      new Stoner("Himanshu", Paper),
      new Stoner("Prabu", Weed),
      new Stoner("Selva", Matches),
      new Stoner("Jim", Paper),
      new Stoner("Siva", Weed),
      new Stoner("Kannan", Matches)
  )

  val hippyCircle = HippyCircle(stoners)
  for (stoner <- stoners) {
    stoner.hippyCircle = hippyCircle
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