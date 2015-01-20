package model;

import main.APL;
import akka.actor.ActorRef;
import enums.MessageType;

public class BlackWorkPete extends Pete {

	public BlackWorkPete(ActorRef admin) {
		super(admin);
	}

	@Override
	public void preStart() throws Exception {
		System.out.println("Black Workpete actor nr. " + (getPeteId())
				+ " has started");
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
				admin.tell(MessageType.NOTIFY_AVAILABLE_BLACK, self());
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
