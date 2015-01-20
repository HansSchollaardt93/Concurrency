package model;

import java.util.ArrayList;

import main.APL;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import enums.MeetingType;
import enums.MessageType;

/**
 * AdministratorPete; The pete that takes care of communicating with the saint,
 * not to disturb him each time a pete is done working and wondering if a
 * meeting is going to take place or not
 * 
 * @author Hans Schollaardt
 *
 */
public class AdministratorPete extends UntypedActor {
	private ArrayList<ActorRef> availableBlackWorkPetes,
			availableRegularWorkPetes, availableGatherPetes;
	private ActorRef saint;
	private int minimumWorkPetes, minimumGatherPetes;
	private boolean meetingInProgress;

	/**
	 * Public constructor of AdministratorPete. Takes care of initializing a few
	 * lists and constant values, to be used elsewhere
	 * 
	 * @param saint
	 *            The saint ActorRef to which the pete can talk to
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
				break;
			case NOTIFY_AVAILABLE_GATHER:

				availableGatherPetes.add(getSender());
				System.err.println("Gatherpetes available: "
						+ availableGatherPetes.size());
				checkMeetingPossible();

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
	 * Method to signal the Saint a meeting is possible; sets a flag
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
			toInvite.add(availableBlackWorkPetes.get(0));
			toInvite.addAll(availableGatherPetes);
			// Get all petes that should join the meeting
			notifyMeeting(MeetingType.GATHER_MEETING, toInvite);

			for (ActorRef actorRef : availableRegularWorkPetes) {
				// Get back to work, come back later
				actorRef.tell(MessageType.WORK, getSelf());
			}
			for (int i = 1; i < availableBlackWorkPetes.size(); i++) {
				availableBlackWorkPetes.get(i)
						.tell(MessageType.WORK, getSelf());
			}
			// they're in meeting or sent back to work now
			availableBlackWorkPetes.clear();
			availableRegularWorkPetes.clear();
			availableGatherPetes.clear();

		} else if (canHaveWorkMeeting()) {
			ArrayList<ActorRef> toInvite = new ArrayList<ActorRef>();
			// Get all petes that should join the meeting
			toInvite.addAll(availableBlackWorkPetes);
			toInvite.addAll(availableRegularWorkPetes);
			// They're in meeting now
			availableBlackWorkPetes.clear();
			availableRegularWorkPetes.clear();
			// Available Gatherpetes will wait untill next meeting
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
