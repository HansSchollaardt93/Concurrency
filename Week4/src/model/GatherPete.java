package model;

import main.APL;
import akka.actor.ActorRef;
import enums.MessageType;

public class GatherPete extends Pete {

	public GatherPete(ActorRef admin) {
		super(admin);
	}

	@Override
	public void preStart() {
		System.out.println("Gatherpete actor nr. " + (getPeteId())
				+ " has started");
	}

	public void postStop() {
		System.out.println("Gatherpete actor exiting");
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MessageType) {
			MessageType received_message = (MessageType) message;
			switch (received_message) {
			case MEETING_IN_PROGRESS: // intended fall through
				System.out
						.println("Meeting already started; going back to work");
			case WORK:
				doWork(APL.getMaxWorkdurationWorkpetes());
				admin.tell(MessageType.NOTIFY_AVAILABLE_GATHER, self());
				break;
			case JOIN_MEETING:
				System.err.println("Called to join meeting");
				break;
			default:
				unhandled(received_message);
				break;
			}
		}
	}

}
