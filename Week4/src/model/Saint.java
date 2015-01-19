package model;

import java.util.HashSet;
import java.util.Set;

import main.APL;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import enums.MeetingType;
import enums.MessageType;

public class Saint extends UntypedActor {
	private MeetingType meetingtype;
	private Set<ActorRef> petes;

	@Override
	public void preStart() {
		System.out.println("Saint actor started");
		petes = new HashSet<ActorRef>();
	}

	public void postStop() {
		System.out.println("Saint actor exiting");
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MessageType) {
			MessageType greeting = (MessageType) message;
			switch (greeting) {
			case HAVE_GATHERMEETING:
				System.out.println("Can have a gathermeeting");
				this.meetingtype = MeetingType.GATHER_MEETING;
				// Tell all the petes to join the meeting
				break;
			case HAVE_WORKMEETING:
				System.out.println("Can have a workmeeting");
				this.meetingtype = MeetingType.WORK_MEETING;

				// Tell all the petes to join the meeting
				break;
			case NOTIFY_INSIDE:
				System.out.println("Im inside");
				// check if amountInside == amountInvited

				// if so; start meeting
				haveMeeting(meetingtype);
				// afterwards, tell them all to get back to work again. Rinse
				// and repeat
				sendBackToWork();
				break;
			default:
				System.out.println("Sorry?");
			}
		}
	}

	private void sendBackToWork() {
		for (ActorRef pete : petes) {
			pete.tell(MessageType.WORK, self());
		}

	}

	/**
	 * 
	 * @param workmeeting
	 *            The type of workmeeting (e.g. a gathermeeting)
	 */
	private void haveMeeting(MeetingType workmeeting) {
		try {
			int DURATION = (int) (Math.random()
					* (APL.getMaxMeetingTime() * 1000) + APL
					.getMinWorkduration() * 1000);
			System.err.println("The Saint has started a "
					+ workmeeting.toString().toLowerCase()
					+ ". This will take  " + (DURATION / 1000)
					+ " seconds! Old man are slow...");
			Thread.sleep(DURATION);
			System.err.println("<--- The meeting has ended --->");
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err
					.println("Saints meeting has been interrupted. The old man won't be amused!");
		}
	}

}
