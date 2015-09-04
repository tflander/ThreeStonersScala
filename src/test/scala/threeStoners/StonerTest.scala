package threeStoners
import org.scalatest._
import scala.util.Random
import java.util.Date

class StonerTest extends FunSpec with ShouldMatchers with BeforeAndAfter {

  describe("Stoner Tests") {
    
    
    class StonerSpy(s: SmokingSupply, c: Seq[SmokingSupply], var messageCountToProcess: Int = 0) extends Stoner(s, c) {
      var lastMessage: Message = null
      
      override def processMessage(message: Message) = {
        if(messageCountToProcess > 0) {
          messageCountToProcess -= 1
          super.processMessage(message)
        } else {
        	lastMessage = message          
        }
      }
    }
    
    val circleOrder = Seq(Paper, Weed, Matches)

    var out: java.io.ByteArrayOutputStream = null
    var paperGuy: Stoner = null
    var weedGuy: Stoner = null
    var matchesGuy: Stoner = null

    before {
      out = new java.io.ByteArrayOutputStream
      Console.setOut(out)
      paperGuy = new StonerSpy(Paper, circleOrder)
      weedGuy = new StonerSpy(Weed, circleOrder, messageCountToProcess = 1)
      matchesGuy = new StonerSpy(Matches, circleOrder)
      val stoners = Seq(paperGuy, weedGuy, matchesGuy)

      for (stoner <- stoners) {
        stoner.stoners = stoners
      }
      
      for (stoner <- stoners) {
        stoner.start()
      }

    }

    it("handles message to request supply") {
      weedGuy.processMessage(Message(Paper, Weed, "requestSupply"))
      out.toString should be("Paper guy requested supply from Weed guy\n")
      getMessage(paperGuy) should be (Message(Weed, Paper, "takeSupply"))
    }
    
    it("handles first message to take supply") {
      weedGuy.processMessage(Message(Paper, Weed, "takeSupply"))
      out.toString should be("Weed guy takes supply from Paper guy\n")
      weedGuy.supplyCount should be(1)
    }

    it("handles second message to take supply") {
      weedGuy.asInstanceOf[StonerSpy].messageCountToProcess = 2
      weedGuy.processMessage(Message(Paper, Weed, "takeSupply"))
      weedGuy.processMessage(Message(Matches, Weed, "takeSupply"))
      weedGuy.supplyCount should be(2)
      getMessage(weedGuy) should be (Message(Weed, Weed, "roll"))
    }

    it("handles message to take a passed joint") {
      weedGuy.processMessage(Message(Paper, Weed, "hitJoint11"))
      out.toString should be("Weed guy takes joint with 11 hits left from Paper guy and tokes\n")
      getMessage(matchesGuy) should be (Message(Weed, Matches, "hitJoint10"))

    }

    it("handles message to toke a newly rolled joint") {
      weedGuy.processMessage(Message(Weed, Weed, "hitJoint11"))
      out.toString should be("Weed guy takes a toke\n")
      getMessage(matchesGuy) should be (Message(Weed, Matches, "hitJoint10"))
    }

    it("handles message to take the last hit of a joint") {
      weedGuy.processMessage(Message(Paper, Weed, "hitJoint1"))
      out.toString should be("Weed guy takes joint with 1 hits left from Paper guy and tokes\n")
      getMessage(weedGuy) should be (Message(Weed, Weed, "yourTurnToRoll"))
    }

    it("handles message to begin rolling a joint") {
      weedGuy.processMessage(Message(Weed, Weed, "yourTurnToRoll"))
      out.toString should be("Weed guy needs to roll a joint\n")
      getMessage(paperGuy) should be (Message(Weed, Paper, "requestSupply"))
      getMessage(matchesGuy) should be (Message(Weed, Matches, "requestSupply"))
      weedGuy.supplyCount should be(0)
    }

    it("handles message to roll a joint") {
      weedGuy.processMessage(Message(Weed, Weed, "roll"))

      out.toString should fullyMatch regex ("Weed guy rolls a \\d+ hit joint and lights it\n")
      val msg = getMessage(weedGuy)

      msg.from should be(Weed)
      msg.to should be(Weed)
      msg.message should fullyMatch regex ("hitJoint\\d+")
    }

    def getMessage(stoner: Stoner): Message = {
      Thread.sleep(30)
      return stoner.asInstanceOf[StonerSpy].lastMessage
    }

    
  }
}