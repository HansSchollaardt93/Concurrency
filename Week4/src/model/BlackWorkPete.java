package model;

import main.APL;
import akka.actor.ActorRef;
import enums.MessageType;

/**
 * Black workpete class, consisting of all the essential logic a Gatherpete
 * needs to function in this program, within the scope of this excersice.
 * Extends Pete, superclass, which holds more generic variables and methods, to
 * be used within this class
 * 
 * @author Hans Schollaardt
 *
 */
public class BlackWorkPete extends Pete {

	/**
	 * Public constructor of BlackWorkPete
	 * 
	 * @param admin
	 *            ActorRef of admin, passed on to superclass
	 * @param saint
	 *            ActorRef of saint, passed on to superclass
	 */
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
		assert (message != null) : "Message cannot be null";
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
