import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class AKKATest {
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("HIApp");
		ActorRef hiActor = system.actorOf(Props.create(HiActor.class),
				"HiActor");
		hiActor.tell(Message.HI, null);
		/* second argument is ActorRef of sender */
		hiActor.tell(Message.HELLO, null);
		hiActor.tell(Message.NO_GREETING, null);
		hiActor.tell(Message.INSULT, null);

		system.shutdown();
	}
}