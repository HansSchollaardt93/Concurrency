package main;

import model.AdministratorPete;
import model.BlackWorkPete;
import model.GatherPete;
import model.Saint;
import model.WorkPete;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import enums.MessageType;

/**
 * Main class which instantiates the running process of the Actor system, in
 * which various Petes and one Saint live and carry out their work
 * 
 * @author Hans Schollaardt
 *
 */
public class APL {
	// Constant values
	public static final int NR_OF_GATHERPETES = 0,
			NR_OF_NORMAL_WORKPETES = 10, NR_OF_BLACK_WORKPETES = 10,
			MAX_MEETING_TIME = 10, MAX_WORKDURATION_GATHERPETES = 20,
			MAX_WORKDURATION_WORKPETES = 40, MIN_WORKDURATION = 0;
	// Minimum amount of petes needed to start a meeting
	private static final int MIN_WORKPETES_NEEDED = 3,
			MIN_GATHERPETES_NEEDED = 3;

	private static ActorSystem system;

	public static void main(String[] args) {
		system = ActorSystem.create("saintsworkers");

		// Create the pete actors
		createActors();
	}

	/**
	 * Method for initiating the Actors in the system. Differentiates between 3
	 * types of Petes; Regular workpetes, black workpetes and gatherpetes. Of
	 * each a predefined constant amount are created, see instance variables on
	 * top.
	 */
	private static void createActors() {
		ActorRef saint = system.actorOf(Props.create(Saint.class), "saint");
		ActorRef administratorPete = system.actorOf(
				Props.create(AdministratorPete.class, saint),
				"administrationPete");
		// Make the saint aware of the presence of the administrator pete
		saint.tell(MessageType.MEET_SAINT, administratorPete);

		for (int i = 0; i < NR_OF_GATHERPETES; i++) {
			ActorRef gatherpete = system.actorOf(
					Props.create(GatherPete.class, administratorPete, saint),
					"Gatherpete" + i);
			gatherpete.tell(MessageType.WORK, null);
		}
		for (int i = 0; i < NR_OF_NORMAL_WORKPETES; i++) {
			ActorRef workpete = system.actorOf(
					Props.create(WorkPete.class, administratorPete, saint),
					"Workpete" + i);
			workpete.tell(MessageType.WORK, null);
		}

		for (int i = 0; i < NR_OF_BLACK_WORKPETES; i++) {
			ActorRef blackworkpete = system
					.actorOf(Props.create(BlackWorkPete.class,
							administratorPete, saint), "BlackWorkpete" + i);
			blackworkpete.tell(MessageType.WORK, null);
		}
	}

	/*
	 * Various self explanatory getters below
	 */

	public static int getMinWorkduration() {
		return MIN_WORKDURATION;
	}

	public static int getMaxWorkdurationGatherpetes() {
		return MAX_WORKDURATION_GATHERPETES;
	}

	public static int getMaxWorkdurationWorkpetes() {
		return MAX_WORKDURATION_WORKPETES;
	}

	public static int getMaxMeetingTime() {
		return MAX_MEETING_TIME;
	}

	public static int getMinGatherpetesNeeded() {
		return MIN_GATHERPETES_NEEDED;
	}

	public static int getMinWorkpetesNeeded() {
		return MIN_WORKPETES_NEEDED;
	}

}
