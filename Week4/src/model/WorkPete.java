package model;

import main.APL;
import akka.actor.ActorRef;
import enums.MessageType;

/**
 * Workpete class, consisting of all the essential logic a Gatherpete needs to
 * function in this program, within the scope of this excersice. Extends Pete,
 * superclass, which holds more generic variables and methods, to be used within
 * this class
 * 
 * @author Hans Schollaardt
 *
 */
public class WorkPete extends Pete {
	/**
	 * Public constructor of BlackWorkPete
	 * 
	 * @param admin
	 *            ActorRef of admin, passed on to superclass
	 * @param saint
	 *            ActorRef of saint, passed on to superclass
	 */
	public WorkPete(ActorRef adminpete, ActorRef saint) {
		super(adminpete, saint);
	}

	@Override
	public void preStart() {
		System.out.println("Workpete actor nr. " + (getPeteId())
				+ " has started");
	}

	public void postStop() {
		System.out.println("Workpete actor exiting");
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
				admin.tell(MessageType.NOTIFY_AVAILABLE_WORK, self());
				break;
			case JOIN_MEETING:
				System.err.println("Regular workpete " + getPeteId()
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
