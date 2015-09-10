package threeStoners
import org.scalatest._
import scala.util.Random
import java.util.Date

class StonerTest extends FunSpec with ShouldMatchers with BeforeAndAfter {

  describe("Three Stoner Tests") {

    val circleOrder = Seq(Paper, Weed, Matches)

    var out: java.io.ByteArrayOutputStream = null
    var paperGuy: Stoner = null
    var weedGuy: Stoner = null
    var matchesGuy: Stoner = null

    before {
      out = new java.io.ByteArrayOutputStream
      paperGuy = new StonerSpy("Himanshu", Paper, o=out)
      weedGuy = new StonerSpy("Prabu", Weed, messageCountToProcess = 1, out)
      matchesGuy = new StonerSpy("Selva", Matches, o=out)
      init(Seq(paperGuy, weedGuy, matchesGuy))
    }

    it("handles message to request supply") {
      weedGuy.processMessage(Message("Himanshu", "Prabu", "requestSupply"))
      out.toString should be("Himanshu requested weed from Prabu\n")
      getMessage(paperGuy) should be(Message("Prabu", "Himanshu", "takeSupply"))
    }

    it("handles first message to take supply") {
      weedGuy.processMessage(Message("Himanshu", "Prabu", "takeSupply"))
      out.toString should be("Prabu takes paper from Himanshu\n")
      weedGuy.supplyCount should be(1)
    }

    it("handles second message to take supply") {
      weedGuy.asInstanceOf[StonerSpy].messageCountToProcess = 2
      weedGuy.processMessage(Message("Himanshu", "Prabu", "takeSupply"))
      weedGuy.processMessage(Message("Selva", "Prabu", "takeSupply"))
      weedGuy.supplyCount should be(2)
      getMessage(weedGuy) should be(Message("Prabu", "Prabu", "roll"))
    }

    it("handles message to take a passed joint") {
      weedGuy.processMessage(Message("Himanshu", "Prabu", "hitJoint11"))
      out.toString should be("Prabu takes joint with 11 hits left from Himanshu and tokes\n")
      getMessage(matchesGuy) should be(Message("Prabu", "Selva", "hitJoint10"))

    }

    it("handles message to toke a newly rolled joint") {
      weedGuy.processMessage(Message("Prabu", "Prabu", "hitJoint11"))
      out.toString should be("Prabu takes a toke\n")
      getMessage(matchesGuy) should be(Message("Prabu", "Selva", "hitJoint10"))
    }

    it("handles message to take the last hit of a joint") {
      weedGuy.processMessage(Message("Himanshu", "Prabu", "hitJoint1"))
      out.toString should be("Prabu takes joint with 1 hits left from Himanshu and tokes\n")
      getMessage(weedGuy) should be(Message("Prabu", "Prabu", "yourTurnToRoll"))
    }

    it("handles message to begin rolling a joint") {
      weedGuy.processMessage(Message("Prabu", "Prabu", "yourTurnToRoll"))
      out.toString should be("Prabu needs to roll a joint\n")
      getMessage(paperGuy) should be(Message("Prabu", "Himanshu", "requestSupply"))
      getMessage(matchesGuy) should be(Message("Prabu", "Selva", "requestSupply"))
      weedGuy.supplyCount should be(0)
    }

    it("handles message to roll a joint") {
      weedGuy.processMessage(Message("Prabu", "Prabu", "roll"))

      out.toString should fullyMatch regex ("Prabu rolls a \\d+ hit joint and lights it\n")
      val msg = getMessage(weedGuy)

      msg.from should be("Prabu")
      msg.to should be("Prabu")
      msg.message should fullyMatch regex ("hitJoint\\d+")
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
    for (stoner <- stoners) {
      stoner.stoners = stoners
    }

    for (stoner <- stoners) {
      stoner.start()
    }
  }

  def getMessage(stoner: Stoner): Message = {
    Thread.sleep(30)
    return stoner.asInstanceOf[StonerSpy].lastMessage
  }

}