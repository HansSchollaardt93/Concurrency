package model;

import java.util.ArrayList;

import main.APL;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import enums.MeetingType;
import enums.MessageType;

public class AdministratorPete extends UntypedActor {
	private ArrayList<ActorRef> availableBlackWorkPetes,
			availableRegularWorkPetes, availableGatherPetes;
	private ActorRef saint;
	private int minimumWorkPetes, minimumGatherPetes;
	private boolean meetingInProgress;

	/**
	 * 
	 * @param saint
	 */
	public AdministratorPete(ActorRef saint) {
		this.saint = saint;

		availableBlackWorkPetes = new ArrayList<ActorRef>();
		availableRegularWorkPetes = new ArrayList<ActorRef>();
		availableGatherPetes = new ArrayList<ActorRef>();

		minimumGatherPetes = APL.getMinGatherpetesNeeded();
		minimumWorkPetes = APL.getMinWorkpetesNeeded();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MessageType) {
			MessageType received_message = (MessageType) message;
			switch (received_message) {
			case NOTIFY_AVAILABLE_BLACK:
				if (!meetingInProgress) {
					availableBlackWorkPetes.add(getSender());
					System.err.println("Black workpetes available: "
							+ availableBlackWorkPetes.size());
					checkMeetingPossible();
				} else {
					getSender()
							.tell(MessageType.MEETING_IN_PROGRESS, getSelf());
				}
				break;
			case NOTIFY_AVAILABLE_WORK:
				if (!meetingInProgress) {
					availableRegularWorkPetes.add(getSender());
					System.err.println("Regular workpetes available: "
							+ availableRegularWorkPetes.size());
					checkMeetingPossible();
				} else {
					getSender()
							.tell(MessageType.MEETING_IN_PROGRESS, getSelf());
				}
			case NOTIFY_AVAILABLE_GATHER:
				if (!meetingInProgress) {
					availableGatherPetes.add(getSender());
					System.err.println("Gatherpetes available: "
							+ availableGatherPetes.size());
					checkMeetingPossible();
				} else {
					getSender()
							.tell(MessageType.MEETING_IN_PROGRESS, getSelf());
				}
				break;
			case NOTIFY_MEETING_STARTED:
				meetingInProgress = true;
				break;
			case NOTIFY_MEETING_ENDED:
				meetingInProgress = false;
				break;
			default:
				unhandled(received_message);
				break;
			}
		}
	}

	/**
	 * Method to signal the Saint a meeting is possible
	 * 
	 * @param type
	 *            The type of meeting that is possible
	 */
	private void notifyMeeting(MeetingType type,
			final ArrayList<ActorRef> toInvite) {
		switch (type) {
		case GATHER_MEETING:
			saint.tell(MessageType.HAVE_GATHERMEETING, getSelf());
			saint.tell(toInvite, getSelf());
			break;
		case WORK_MEETING:
			saint.tell(MessageType.HAVE_WORKMEETING, getSelf());
			saint.tell(toInvite, getSelf());
			break;
		default:
			break;
		}
	}

	/**
	 * Method to check if a meeting is possible. If so; create a list of
	 * invitees and send this to the saint, so he can invite them to the
	 * meeting. Send the ones not invited back to work
	 */
	public void checkMeetingPossible() {
		if (canHaveGatherMeeting()) {
			ArrayList<ActorRef> toInvite = new ArrayList<ActorRef>();
			ArrayList<ActorRef> tempBlack = (ArrayList<ActorRef>) availableBlackWorkPetes
					.clone();
			// Get all petes that should join the meeting

			// Release all that are left behind
			notifyMeeting(MeetingType.GATHER_MEETING, toInvite);
		} else if (canHaveWorkMeeting()) {
			ArrayList<ActorRef> toInvite = new ArrayList<ActorRef>();
			// Get all petes that should join the meeting

			// Release all that are left behind
			notifyMeeting(MeetingType.WORK_MEETING, toInvite);
		}
	}

	/**
	 * Helper method the determine if a meeting can start Required: At least 1
	 * black workerpete, all gatherpetes available with a minimum of 3, and the
	 * saint
	 * 
	 * @return true if complies with requirements, false otherwise
	 */
	private boolean canHaveGatherMeeting() {
		assert (availableBlackWorkPetes != null) : "list is not initialized!";
		assert (availableRegularWorkPetes != null) : "list is not initialized!";
		return (availableBlackWorkPetes.size() >= 1 && availableGatherPetes
				.size() >= minimumGatherPetes);
	}

	/**
	 * Helper method the determine if a meeting can start
	 * 
	 * Required: Not enough (MIN_GATHERPETES_NEEDED) or no black workpete
	 * available to start gathermeeting, but there are enough workerpetes (>=
	 * MIN_WORKPETES_NEEDED) available;
	 * 
	 * @return true if complies with requirements, false otherwise
	 */
	private boolean canHaveWorkMeeting() {
		return (availableBlackWorkPetes.size() + availableRegularWorkPetes
				.size()) > minimumWorkPetes;
	}
}
