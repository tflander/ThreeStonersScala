package threeStoners
import org.scalatest._
import scala.util.Random
import java.util.Date

class StonerTest extends FunSpec with ShouldMatchers with BeforeAndAfter {

  describe("Six Stoner Tests") {

    var out: java.io.ByteArrayOutputStream = null
    var paperGuy1: Stoner = null
    var weedGuy1: Stoner = null
    var matchesGuy1: Stoner = null
    var paperGuy2: Stoner = null
    var weedGuy2: Stoner = null
    var matchesGuy2: Stoner = null

    before {
      out = new java.io.ByteArrayOutputStream
      paperGuy1 = new StonerSpy("Himanshu", Paper, o = out)
      weedGuy1 = new StonerSpy("Prabu", Weed, messageCountToProcess = 1, out)
      matchesGuy1 = new StonerSpy("Selva", Matches, o = out)
      paperGuy2 = new StonerSpy("Ram", Paper, o = out)
      weedGuy2 = new StonerSpy("Siva", Weed, messageCountToProcess = 1, out)
      matchesGuy2 = new StonerSpy("Jim", Matches, o = out)
      init(Seq(paperGuy1, weedGuy1, matchesGuy1, paperGuy2, weedGuy2, matchesGuy2))
    }

    it("requests from only one stoner with a particular supply when handling message to begin rolling a joint") {
      weedGuy1.processMessage(Message("Prabu", "Prabu", "yourTurnToRoll"))
      out.toString should be("Prabu needs to roll a joint\n")

      val stonerProvidingPaper = Seq(getMessage(paperGuy1), getMessage(paperGuy2))
        .flatten
        .map(msg => msg.to)

      stonerProvidingPaper.size should be(1)
      stonerProvidingPaper should contain oneOf ("Himanshu", "Ram")

      val stonerProvidingMatches = Seq(getMessage(matchesGuy1), getMessage(matchesGuy2))
        .flatten
        .map(msg => msg.to)

      stonerProvidingMatches.size should be(1)
      stonerProvidingMatches should contain oneOf ("Selva", "Jim")
    }

    // TODO:  how to write this test?
    ignore("can request from any stoner with supply when handling message to begin rolling a joint") {

      val testResults = (1 to 10).map { i =>
        weedGuy1.processMessage(Message("Prabu", "Prabu", "yourTurnToRoll"))
        val paperGuys = Seq(getMessage(paperGuy1), getMessage(paperGuy2))
          .flatten
          .map(msg => msg.to)
          .toSet

        val matchesGuys = Seq(getMessage(matchesGuy1), getMessage(matchesGuy2))
          .flatten
          .map(msg => msg.to)
          .toSet
        (paperGuys, matchesGuys)
      }

      val paperGuys = testResults.map(_._1).flatten.toSet
      val matchesGuys = testResults.map(_._2).flatten.toSet

      paperGuys should be(Set("Himanshu", "Ram"))
      matchesGuys should be(Set("Selva", "Jim"))

      weedGuy1.supplyCount should be(0)
    }

  }

  class StonerSpy(i: String, s: SmokingSupply, var messageCountToProcess: Int = 0, o: java.io.ByteArrayOutputStream = null) extends Stoner(i, s, o) {
    var lastMessage: Message = null

    override def processMessage(message: Message) = {
      if (messageCountToProcess > 0) {
        messageCountToProcess -= 1
        super.processMessage(message)
      } else {
        lastMessage = message
      }
    }
  }

  def init(stoners: Seq[Stoner]) {
    val hippyCircle = HippyCircle(stoners)
    for (stoner <- stoners) {
      stoner.hippyCircle = hippyCircle
    }

    for (stoner <- stoners) {
      stoner.start()
    }
  }

  def getMessage(stoner: Stoner): Option[Message] = {
    Thread.sleep(30)
    stoner.asInstanceOf[StonerSpy].lastMessage match {
      case null => None
      case msg => Some(msg)
    }
  }

}