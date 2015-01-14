package model;

import java.util.concurrent.Semaphore;

/**
 * Saintsmansion; the house of the old, wise, white bearded saint. He travels
 * from Spain to Holland with all of his petes, where they gather the children's
 * wish lists and take care of presenting them with their desired gifts.
 * 
 * @author Hans Schollaardt
 */
public class SaintsMansion {
	// Constant values

	private static final int NR_OF_GATHERPETES = 5, NR_OF_WORKPETES = 7,
			MAX_MEETING_TIME = 20, MAX_WORKDURATION_GATHERPETES = 40,
			MAX_WORKDURATION_WORKPETES = 20;
	// one in 'how-many' should be black petes?
	private static final int MOD_VALUE_BLACK_PETES = 4,
			MIN_WORKPETES_NEEDED = 3, MIN_GATHERPETES_NEEDED = 3;

	// Naming and meeting counters
	private int blackPeteCount, workPeteCount, regularWorkpetesInMeeting,
			blackWorkpetesInMeeting, gatherPetesInMeeting;
	// Availability Counters
	private int availableWorkPetes, availableBlackWorkPetes,
			availableGatherPetes;
	private MeetingFlag flag;
	private boolean meetingInProgress = false;
	private Thread[] gatherpetes, workerpetes;
	public Semaphore readyForGathermeeting, readyForWorkmeeting,
			readyForWorkmeetingBlack, meetingMutexGather, workMutex,
			gatherMutex, saint, meetingMutexBlack, haveMeeting;
	public Semaphore seatTaken;

	/**
	 * Constructor of Saintsmansion; responsible for initializing Threads,
	 * Semaphores and Object
	 */
	public SaintsMansion() {
		gatherpetes = new Thread[NR_OF_GATHERPETES];
		workerpetes = new Thread[NR_OF_WORKPETES];

		readyForGathermeeting = new Semaphore(0, true);
		readyForWorkmeeting = new Semaphore(0, true);
		readyForWorkmeetingBlack = new Semaphore(0, true);

		workMutex = new Semaphore(1, true);
		gatherMutex = new Semaphore(1, true);
		// initially no saint available, let the petes awake him
		saint = new Semaphore(1);

		createPeteThreads();

		// Initialize Saint
		Saint saint = new Saint();
		saint.start();
	}

	/**
	 * Initial setup of all the Pete threads, differentiating the Petes in Work-
	 * and Gatherpetes, where Workpetes are either "uncolored", or "black". When
	 * a workpete is black, he's seen as experienced, and can attend
	 * Gathermeetings.
	 */
	private void createPeteThreads() {
		for (int i = 0; i < NR_OF_GATHERPETES; i++) {
			gatherpetes[i] = new GatherPete("Gatherpete " + i);
			gatherpetes[i].start();
		}
		for (int i = 0; i < NR_OF_WORKPETES; i++) {
			// for one in n-petes, create a black pete
			if (i % MOD_VALUE_BLACK_PETES == 0) {
				workerpetes[i] = new WorkPete("Black pete " + blackPeteCount++,
						true);
			} else {
				workerpetes[i] = new WorkPete("Workerpete " + workPeteCount++,
						false);
			}
			workerpetes[i].start();
		}

	}

	/**
	 * The Saint; an old wise, but tired man. One that sleeps, or when woken up
	 * by petes organizes Work- or Gathermeetings.
	 * 
	 * @author Hans Schollaardt
	 * 
	 */
	class Saint extends Thread {

		

		@Override
		public void run() {
			while (true) {
				try {
					saint.acquire();
					if (canAttendGatherMeeting()) {
						// Go have a GatherMeeting
						attendGatherMeeting();
					} else if (canAttendWorkMeeting()) {
						// Go have a WorkMeeting
						attendWorkMeeting();
					} else {

					}
					saint.release();
				} catch (InterruptedException e) {
					System.out
							.println("Uh-oh, the Saint is interrupted in his business!");
					e.printStackTrace();
				}

			}
		}

		/**
		 * Workmeeting in which the saint gathers a minimum of 3 workpetes is
		 * available, no or not enough gatherpetes available
		 */
		private void attendWorkMeeting() throws InterruptedException {
			/*
			 * acquire the desired petes, here: all the workpetes black or not,
			 * but NO gatherpetes
			 */
			meetingInProgress = true;
			workMutex.acquire();
			// save how many petes were taken into meeting; critical section
			regularWorkpetesInMeeting = availableWorkPetes;
			blackWorkpetesInMeeting = availableBlackWorkPetes;
			workMutex.release();
			// Attend the actual meeting
			haveMeeting(Worktype.WORKMEETING);
			// afterwards release them
			workMutex.acquire();
			// release those that attended the meeting
			readyForWorkmeeting.release(regularWorkpetesInMeeting
					+ blackWorkpetesInMeeting);
			// reset counters
			regularWorkpetesInMeeting = 0;
			blackWorkpetesInMeeting = 0;
			workMutex.release();
			meetingInProgress = false;
		}

		/**
		 * Gathermeeting in which the saint gathers a minimum of 3 Gatherpetes
		 * and exactly 1 black workpete.
		 */
		//TODO, reset the counters
		private void attendGatherMeeting() throws InterruptedException {
			/*
			 * Take the appropriate ammount of petes with you to the meeting.
			 * Acquire the number of petes, create the mutex with n-amount of
			 * available permits, and wait until all the petes are ready for the
			 * meeting
			 */
			int gatherpetesInMeeting = availableGatherPetes;
			// create new semaphore with the needed petes as amount of permits
			// (e.g. 1 pete needed == 1 permit)
			meetingMutexBlack.release(gatherpetesInMeeting);
			meetingMutexGather.release(1);
			// set flag to announce to petes coming back from work they do not
			// need to enter the queue
			flag = MeetingFlag.GATHERMEETING;
			meetingInProgress = true;
			/* release all waiting petes, let them decide if they need to enter and wait untill the right petes have entered the
			 meeting*/
			readyForGathermeeting.release(availableGatherPetes);
			readyForWorkmeetingBlack.release(availableBlackWorkPetes);
			
			//after all the expected seats are taken, all petes are inside and we can start the meeting
			seatTaken.acquire(gatherpetesInMeeting+1);
						
			// Thread sleep to simulate meeting
			haveMeeting(Worktype.GATHERMEETING);
			
			haveMeeting.release(gatherpetesInMeeting + 1);
			//reset the flag 
			flag = MeetingFlag.NO_MEETING;
		}
		

		/**
		 * Method to return boolean if the conditions allow a Gathermeeting to
		 * take place
		 * 
		 * @return true; enough gather- and the right workpete is available
		 *         false; other situations
		 */
		private boolean canAttendGatherMeeting() {
			return (availableGatherPetes >= MIN_GATHERPETES_NEEDED && availableBlackWorkPetes >= 1);
		}

		/**
		 * Method to return boolean the conditions allow a Gathermeeting to take
		 * place
		 * 
		 * @return true; enough workpetes are available false; not enough
		 *         workpetes are available
		 */
		private boolean canAttendWorkMeeting() {
			return (availableWorkPetes + availableBlackWorkPetes >= MIN_WORKPETES_NEEDED);
		}

		/**
		 * 
		 * @param workmeeting
		 *            The type of workmeeting (e.g. a gathermeeting)
		 */
		private void haveMeeting(Worktype workmeeting) {
			try {
				int MAXDURATION = (int) (Math.random()
						* (MAX_MEETING_TIME * 1000) + 5000);
				System.err.println("The Saint has started a "
						+ workmeeting.toString().toLowerCase()
						+ ". This will take up to " + (MAXDURATION / 1000)
						+ " seconds! Old man are slow...");
				Thread.sleep(MAXDURATION);
				System.err.println("<--- The meeting has ended --->");
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err
						.println("Saints meeting has been interrupted, the old man won't be amused!");
			}
		}
	}

	/**
	 * Gatherpete; a subclass of the Pete class. This contains all logic for
	 * fulfilling their work gathering wishlists. Those the Workpetes in their
	 * turn need to buy or create the appropriate gifts.
	 * 
	 * @author Hans Schollaardt
	 * 
	 */
	class GatherPete extends Pete {
		public GatherPete(String name) {
			super(name);
		}

		@Override
		public void run() {
			while (true) {
				doWork(MAX_WORKDURATION_GATHERPETES);
				try {
					gatherMutex.acquire();
					availableGatherPetes++;
					System.err.println("Gatherpetes available: "
							+ availableGatherPetes);
					gatherMutex.release();

					// wake up the saint, let him check if meeting is
					// possible! <-- avoid busy-waiting
					saint.release();
					readyForGathermeeting.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * The Workpete representation of the Saints helpers. A workpete buys and/or
	 * creates presents that are on the wishlists the Gatherpetes collected.
	 * Subclass of Pete that contains all base logic for a Pete such as name and
	 * ID.
	 * 
	 * @author Hans Schollaardt
	 * 
	 */
	class WorkPete extends Pete {
		private boolean black = false;

		public WorkPete(String name, boolean black) {
			super(name);
			this.black = black;
		}

		@Override
		public void run() {
			while (true) {
				doWork(MAX_WORKDURATION_WORKPETES);
				// If a meeting is already in progress, continue work
				if (!meetingInProgress) {
					try {
						if (this.isBlack()) {
							workMutex.acquire();
							assert (availableBlackWorkPetes + 1 <= NR_OF_WORKPETES
									/ MOD_VALUE_BLACK_PETES);
							availableBlackWorkPetes++;
							workMutex.release();
							System.err.println("Black Workpetes available: "
									+ availableBlackWorkPetes);
							// wake up the saint, let him check if meeting is
							// possible!
							saint.release();
							//sit down and wait untill called
							readyForWorkmeetingBlack.acquire();
						} else if (!this.black) {
							workMutex.acquire();
							assert (availableWorkPetes <= NR_OF_WORKPETES
									- (NR_OF_WORKPETES / MOD_VALUE_BLACK_PETES));
							availableWorkPetes++;
							workMutex.release();
							System.err.println("Regular Workpetes available: "
									+ availableWorkPetes);
							// wake up the saint, let him check if meeting is
							// possible!
							saint.release();
							//sit down and wait untill called
							readyForWorkmeeting.acquire();
						}

						// TODO NIEUW!
						if (flag == MeetingFlag.GATHERMEETING) {
							// attend Gathermeeting, acquire one permit as it is
							// needed
							// acquire a spot in the meeting
							if (isBlack() && meetingMutexBlack.tryAcquire(1)) {
							//wait untill meeting is over
								seatTaken.release();
								haveMeeting.acquire();								
							} else if (!isBlack()
									&& meetingMutexGather.tryAcquire(1)) {
								//wait untill meeting is over
								seatTaken.release();
								haveMeeting.acquire();
								// continue to work
							} else {
								break;
							}
						} else if (flag == MeetingFlag.WORKMEETING) {
							// If this pete is a workpete (either way black or
							// not) and there's a spot open to join the meeting
							if ((this instanceof WorkPete)
									&& meetingMutexBlack.tryAcquire(1)) {
								seatTaken.release();
								// enter meeting
								haveMeeting.acquire();
								// no spots open, continue to work
							} else {
								// nothing to do here, get back to work
								break;
							}
						} else if (flag == MeetingFlag.NO_MEETING) {
							// no meeting; continue to work
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Meeting already started, " + this
							+ " is going to work again...");
				}
			}
		}

		/**
		 * 
		 * @return true; a workpete is black by going down chimneys false; a
		 *         workpete is of another skincolour
		 */
		public boolean isBlack() {
			return black;
		}
	}

	/**
	 * Enum representing the different types of work a pete will be carrying out
	 * 
	 * @author Hans Schollaardt
	 * 
	 */
	enum Worktype {
		WORKMEETING, GATHERMEETING
	}

	enum MeetingFlag {
		WORKMEETING, GATHERMEETING, NO_MEETING
	}
}
