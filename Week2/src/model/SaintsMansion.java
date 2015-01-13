package model;
import java.util.concurrent.Semaphore;

/**
 * Saintsmansion; the house of the old, wise, white bearded saint. He travels
 * from Spain to Holland with all of his petes, where they gather the children's
 * wish lists and take care of presenting them with their desired gifts.
 * 
 * 'Peppernuts' are not included in this program, nor were they consumed
 * during construction :)
 * 
 * @author Hans Schollaardt
 */
public class SaintsMansion {
	// Constant values

	private static final int NR_OF_GATHERPETES = 5, NR_OF_WORKPETES = 7,
			MAX_MEETING_TIME = 20, MAX_WORKDURATION_GATHERPETES = 40, MAX_WORKDURATION_WORKPETES = 20;
	// one in 'how-many' should be black petes?
	private static final int MOD_VALUE_BLACK_PETES = 4, MIN_WORKPETES_NEEDED = 3,
			MIN_GATHERPETES_NEEDED = 3;
	
	// Naming and meeting counters 
	private int blackPeteCount, workPeteCount, regularWorkpetesInMeeting, blackWorkpetesInMeeting, gatherPetesInMeeting;
	// Availability Counters
	private int availableWorkPetes, availableBlackWorkPetes,
			availableGatherPetes;

	private boolean meetingInProgress = false;
	private Thread[] gatherpetes, workerpetes;
	public Semaphore readyForGathermeeting, readyForWorkmeeting, workMutex,
			gatherMutex, saint;

	/**
	 * Constructor of Saintsmansion; responsible for initializing Threads,
	 * Semaphores and Object
	 */
	public SaintsMansion() {
		gatherpetes = new Thread[NR_OF_GATHERPETES];
		workerpetes = new Thread[NR_OF_WORKPETES];

		readyForGathermeeting = new Semaphore(0, true);
		readyForWorkmeeting = new Semaphore(0, true);

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
			//save how many petes were taken into meeting; critical section
			regularWorkpetesInMeeting = availableWorkPetes;
			blackWorkpetesInMeeting = availableBlackWorkPetes;
			workMutex.release();
			// Attend the actual meeting
			haveMeeting(Worktype.WORKMEETING);
			// afterwards release them
			workMutex.acquire();
			//release those that attended the meeting
			readyForWorkmeeting.release(regularWorkpetesInMeeting + blackWorkpetesInMeeting);
			//reset counters
			regularWorkpetesInMeeting = 0;
			blackWorkpetesInMeeting = 0;
			workMutex.release();
			meetingInProgress = false;
		}

		/**
		 * Gathermeeting in which the saint gathers a minimum of 3 Gatherpetes
		 * and at least 1 black workpete.
		 */
		private void attendGatherMeeting() throws InterruptedException {
			meetingInProgress = true;
			
			assert(availableGatherPetes>=3 && availableBlackWorkPetes >=1);
			workMutex.acquire();
			if (availableBlackWorkPetes > 1) {
				assert(availableBlackWorkPetes - 1 > 0);
				//take only one black workpete to meeting
				readyForWorkmeeting.release(availableBlackWorkPetes - 1);
			} 
			//Critical section; only the currently available (regular)petes should be released
			readyForWorkmeeting.release(availableWorkPetes);
			
			workMutex.release();
			gatherMutex.acquire();
			gatherPetesInMeeting = availableGatherPetes;
			//send all gatherpetes into meeting
			availableGatherPetes = 0;
			gatherMutex.release();
			// Attend the actual meeting
			haveMeeting(Worktype.GATHERMEETING);
			
			// release all workpetes afterwards
			workMutex.acquire();
			//Just one black pete taken into meeting
			readyForWorkmeeting.release(1);
			workMutex.release();
			// release gatherpetes
			gatherMutex.acquire();
			readyForGathermeeting.release(gatherPetesInMeeting);
			//reset counter
			gatherPetesInMeeting = 0;
			gatherMutex.release();
			meetingInProgress = false;
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
				int MAXDURATION = (int) (Math.random() * (MAX_MEETING_TIME * 1000)) + 5000;
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
							assert(availableBlackWorkPetes + 1 <= NR_OF_WORKPETES/MOD_VALUE_BLACK_PETES);
							availableBlackWorkPetes++;
							workMutex.release();
							System.err.println("Black Workpetes available: "
									+ availableBlackWorkPetes);
						} else if (!this.black) {
							workMutex.acquire();
							assert(availableWorkPetes <= NR_OF_WORKPETES - (NR_OF_WORKPETES/MOD_VALUE_BLACK_PETES));
							availableWorkPetes++;
							workMutex.release();
							System.err.println("Regular Workpetes available: "
									+ availableWorkPetes);
						}
						// wake up the saint, let him check if meeting is possible!
						saint.release();
						//make it a non-blocking operation!
						readyForWorkmeeting.acquire();
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
}
