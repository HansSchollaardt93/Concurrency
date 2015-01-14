import akka.actor.UntypedActor;

public class HiActor extends UntypedActor {
	@Override
	public void preStart() {
		System.out.println("Hi actor started");
	}

	public void postStop() {
		System.out.println("Hi actor exiting");
	}

	/* conGnues on the right => */
	public void onReceive(Object message) throws Exception {
		if (message instanceof Message) {
			Message greeting = (Message) message;
			switch (greeting) {
			case HI:
				System.out.println("Hello");
				break;
			case HELLO:
				System.out.println("Hello too");
				break;
			case NO_GREETING:
				System.out.println("HI");
				break;
			case INSULT:
				System.out.println("Thank You");
				break;
			default:
				System.out.println("Sorry?");
			}
		}
	}

} /* End of class HiActor */