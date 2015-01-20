package model;

import main.APL;
import akka.actor.ActorRef;
import enums.MessageType;

public class BlackWorkPete extends Pete {

	public BlackWorkPete(ActorRef admin, ActorRef saint) {
		super(admin, saint);
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
				System.err.println("Black workpete " + getPeteId()
						+ " called to join meeting");
				saint.tell(MessageType.NOTIFY_JOINED_MEETING, getSelf());
				break;
			default:
				unhandled(received_message);
				break;
			}
		}
	}

}
