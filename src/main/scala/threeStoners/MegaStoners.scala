package threeStoners

object MegaStoners extends App {

  val numStoners = 1000000
  
  def randomSupply(): SmokingSupply = {
       import scala.util.Random
       return Random.nextInt(3) match {
         case 0 => Weed
         case 1 => Paper
         case 2 => Matches
       }
  }
 
  println("creating stoners...")
  val stoners = 1 to numStoners map(i => {
    new Stoner("Stoner"+i, randomSupply())
  }) 

  println("initializing stoners...")
  val hippyCircle = HippyCircle(stoners)  
  for (stoner <- stoners) {
    stoner.hippyCircle = hippyCircle
  }

  val stonerToRoll = stoners.head
  
  val firstMessage = new Message(from=null, to=stonerToRoll.stonerId, "yourTurnToRoll")

  println("Starting stoner threads")
  for (smoker <- stoners) {
    smoker.start()
  }

  stonerToRoll ! firstMessage

  Thread.sleep(10000)

  for (smoker <- stoners) {
    smoker ! "EXIT"
  }

  println("finished")
}