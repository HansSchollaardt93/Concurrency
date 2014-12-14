import java.util.concurrent.Semaphore;

/**
 * Saintsmansion; the house of the old, wise, white bearded saint. He travels
 * from Spain to Holland with all of his petes, where they gather the childrens
 * wishlists and take care of presenting them with their desired gifts.
 * 
 * Peppernuts are not included in this program, neither were they consumed during programming :)
 * 
 * @author Hans Schollaardt
 * 
 */
public class SaintsMansion {
	// Constant values
	private static final int NR_OF_GATHERPETES = 10, NR_OF_WORKPETES = 3,
			MAX_MEETING_TIME = 20;

	// one in how many should be black petes?
	private static final int MOD_VALUE_BLACK_PETES = 3;

	private static final int MIN_WORKPETES_NEEDED = 3,
			MIN_GATHERPETES_NEEDED = 3;

	private static int blackPeteCount, workPeteCount;
	// Counters
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
		public boolean inMeeting = false;

		@Override
		public void run() {
			while (true) {
				try {
					saint.acquire();
					if (canAttendWorkMeeting()) {
						// Go have a WorkMeeting
						System.err.println("Can attend Workmeeting");
						attendWorkMeeting();

					} else if (canAttendGatherMeeting()) {
						// Go have a GatherMeeting
						System.err.println("Can attend Gathermeeting");
						attendGatherMeeting();
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
			 * but no gatherpetes
			 */
			meetingInProgress = true;
			// Attend the actual meeting
			doWork(Worktype.WORKMEETING);
			// afterwards release them
			readyForWorkmeeting.release();
			meetingInProgress = false;
		}

		/**
		 * Gathermeeting in which the saint gathers a minimum of 3 Gatherpetes
		 * and at least 1 black workpete.
		 */
		private void attendGatherMeeting() throws InterruptedException {
			meetingInProgress = true;

			// release those not needed for gathering
			readyForGathermeeting.release(availableGatherPetes
					- MIN_GATHERPETES_NEEDED);
			// TODO !!
			// readyForWorkmeeting.release(availableBlackWorkPetes-1);

			// can release all the petes that are not black
			readyForWorkmeeting.release(availableWorkPetes);

			// Attend the actual meeting
			doWork(Worktype.GATHERMEETING);
			// release all workpetes afterwards
			workMutex.acquire();
			readyForWorkmeeting.release(availableWorkPetes
					+ availableBlackWorkPetes);
			availableWorkPetes = 0;
			availableBlackWorkPetes = 0;
			workMutex.release();
			// release gatherpetes
			gatherMutex.acquire();
			readyForGathermeeting.release();
			availableGatherPetes = 0;
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
		private void doWork(Worktype workmeeting) {
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
				doWork(30);
				// If a meeting is already in progress, continue work
				try {
					gatherMutex.acquire();
					availableGatherPetes++;
					System.err.println("Gatherpetes available: "
							+ availableGatherPetes);
					gatherMutex.release();
					readyForGathermeeting.acquire();
					// wake up the saint, let him check if meeting is
					// possible! <-- avoid busy-waiting
					saint.release();
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
				doWork(30);
				// If a meeting is already in progress, continue work
				if (!meetingInProgress) {
					try {
						if (this.isBlack()) {
							workMutex.acquire();
							availableBlackWorkPetes++;
							workMutex.release();
							System.err.println("Black Workpetes available: "
									+ availableBlackWorkPetes);
							readyForWorkmeeting.acquire();
						} else if (!this.black) {
							workMutex.acquire();
							availableWorkPetes++;
							workMutex.release();
							System.err.println("Regular Workpetes available: "
									+ availableWorkPetes);
							readyForWorkmeeting.acquire();
						}
						// wake up the saint, let him check if meeting is
						// possible!
						saint.release();
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
