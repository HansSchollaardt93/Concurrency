package main;

import model.GatherPete;
import model.Saint;
import model.WorkPete;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * 
 * @author Hans Schollaardt
 *
 */
public class APL {
	// Constant values
	private static final int NR_OF_GATHERPETES = 20, NR_OF_WORKPETES = 8,
			NR_OF_BLACK_GATHERPETES = 3, MAX_MEETING_TIME = 20,
			MAX_WORKDURATION_GATHERPETES = 40, MAX_WORKDURATION_WORKPETES = 20,
			MIN_WORKDURATION = 5;
	// one in 'how-many' should be black petes?
	private static final int MOD_VALUE_BLACK_PETES = 4,
			MIN_WORKPETES_NEEDED = 3, MIN_GATHERPETES_NEEDED = 3;

	private static ActorSystem system;

	public static void main(String[] args) {
		system = ActorSystem.create("saintsworkers");
		ActorRef saint = system.actorOf(Props.create(Saint.class), "saint");
		// Create the pete actors
		createActors();
	}

	private static void createActors() {
		for (int i = 0; i < NR_OF_GATHERPETES; i++) {
			ActorRef gatherpete = system.actorOf(
					Props.create(GatherPete.class), "Gatherpete" + i);
		}
		for (int i = 0; i < NR_OF_WORKPETES; i++) {

			// for one in n-petes, create a black pete
			if (i % MOD_VALUE_BLACK_PETES == 0) {
				ActorRef blackpete = system.actorOf(
						Props.create(WorkPete.class), "Blackworkpete" + i);
			} else {
				ActorRef workpete = system.actorOf(
						Props.create(WorkPete.class), "Workpete" + i);
			}
		}
	}

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

}
