package jp.o3co.domain

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest._

class TestActor(config: Config) extends Actor {
  val receive: Receive = {
    case msg => sender ! msg
  }
}

class ServiceBooterSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll
{
  
  def this() = this(ActorSystem("ServiceBooterSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "ServiceBooter" must {
    "boot service actor with configuration" in {
      
      val config = ConfigFactory.parseString(
"""
class = "o3co.util.ddd.TestActor"
""")

      val service = ServiceBooter(config)
      service should not be empty
      service.get! "hello"
      expectMsg("hello")
    }
  }
}
