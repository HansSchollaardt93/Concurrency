import java.util.concurrent.Semaphore;

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

	/*
	 * 
	 * SAINT
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
						attentGatherMeeting();
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
		 * Werkoverleg: Minimaal 3 werkpieten, GEEN verzamelpiet beschikbaar
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
		 * Verzameloverleg: Minimaal 3 verzamelpieten, minimaal 1 zwarte(!!)
		 * werkpiet beschikbaar
		 */
		private void attentGatherMeeting() throws InterruptedException {
			// acquire the desired petes
			meetingInProgress = true;

			// release those not needed for gathering
			readyForGathermeeting.release(availableGatherPetes
					- MIN_GATHERPETES_NEEDED);
			
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
 * 
 * @return
 */
		private boolean canAttendGatherMeeting() {
			return (availableGatherPetes >= MIN_GATHERPETES_NEEDED && availableBlackWorkPetes >= 1);
		}
/**
 * 
 * @return
 */
		private boolean canAttendWorkMeeting() {
			return (availableWorkPetes + availableBlackWorkPetes >= MIN_WORKPETES_NEEDED);
		}

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

	/*
	 * GATHERPETE
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

	/*
	 * WORKPETE
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

		public boolean isBlack() {
			return black;
		}
	}

	enum Worktype {
		WORKMEETING, GATHERMEETING
	}
}
