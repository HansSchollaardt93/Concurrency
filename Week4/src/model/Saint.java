package model;

import java.util.ArrayList;

import main.APL;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import enums.MeetingType;
import enums.MessageType;

public class Saint extends UntypedActor {
	private MeetingType meetingtype;// flag to show which meeting will take
									// place
	private ArrayList<ActorRef> invitationlist, joinedPetesList;
	private ActorRef adminpete;

	@Override
	public void preStart() {
		System.out.println("Saint actor started");
		joinedPetesList = new ArrayList<ActorRef>();
	}

	public void postStop() {
		System.out.println("Saint actor exiting");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MessageType) {
			MessageType received_message = (MessageType) message;
			switch (received_message) {
			case MEET_SAINT:
				System.err
						.println("Administration Pete has met Saint; he's aware of his function now!");
				this.adminpete = getSender();
				break;
			case HAVE_GATHERMEETING:
				System.out.println("Can have a gathermeeting");
				this.meetingtype = MeetingType.GATHER_MEETING;

				break;
			case HAVE_WORKMEETING:
				System.out.println("Can have a workmeeting");
				this.meetingtype = MeetingType.WORK_MEETING;

				break;
			case NOTIFY_JOINED_MEETING:
				System.out.println(getSender().toString() + "Im inside");
				joinedPetesList.add(getSender());
				/*
				 * check if all have joined the meeting; if so, start the
				 * meeting
				 */
				if (allJoinedMeeting()) {
					adminpete.tell(MessageType.NOTIFY_MEETING_STARTED,
							getSelf());
					haveMeeting(meetingtype);
					adminpete.tell(MessageType.NOTIFY_MEETING_ENDED, getSelf());
					// afterwards, tell them all to get back to work again.
					sendBackToWork();
					// empty the lists for next meeting
					joinedPetesList.clear();
					invitationlist.clear();
				}
				break;
			default:
				unhandled(received_message);
				System.out.println("uh-oh, no such message is known");
			}
		} else if (message instanceof ArrayList<?>) {
			invitationlist = (ArrayList<ActorRef>) message;
			for (ActorRef toInvite : invitationlist) {
				toInvite.tell(MessageType.JOIN_MEETING, getSelf());
			}
		}
	}

	/**
	 * Helpermethod to determine if all invited petes have joined the meeting
	 * 
	 * @return true if all have joined, false otherwise
	 */
	private boolean allJoinedMeeting() {
		return invitationlist.size() == joinedPetesList.size();
	}

	/**
	 * Method to send all petes that joined the meeting back to work
	 */
	private void sendBackToWork() {
		for (ActorRef pete : joinedPetesList) {
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
