import java.util.concurrent.Semaphore;

public class SaintsMansion {
	private static final int NR_OF_GATHERPETES = 10;
	private static final int NR_OF_WORKPETES = 3;

	private static final int WORKPETES_NEEDED_GATHERING = 1;
	private static final int GATHERPETES_NEEDED_GATHERING = 3;

	private int availableWorkPetes = 0;
	private int availableGatherPetes = 0;

	private Thread[] gatherpetes;
	private Thread[] workerpetes;

	public Semaphore gatherPeteMeeting, workPeteMeeting, workMutex,
			gatherMutex;

	public SaintsMansion() {
		gatherpetes = new Thread[NR_OF_GATHERPETES];
		workerpetes = new Thread[NR_OF_WORKPETES];

		// initially no pete's to wait for
		gatherPeteMeeting = new Semaphore(0, true);
		workPeteMeeting = new Semaphore(0, true);
		workMutex = new Semaphore(1, true);
		gatherMutex = new Semaphore(1, true);

		createPeteThreads();

		// Initialize Saint
		Saint saint = new Saint();
		saint.start();
	}

	private void createPeteThreads() {
		for (int i = 0; i < NR_OF_GATHERPETES; i++) {
			gatherpetes[i] = new GatherPete("Gatherpete" + i);
			gatherpetes[i].start();
		}
		for (int i = 0; i < NR_OF_WORKPETES; i++) {
			workerpetes[i] = new WorkPete("Workerpete" + i);
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
				regenerateEnergy();
				attendGathering();
				try {
					if (availableGatherPetes == WORKPETES_NEEDED_GATHERING
							&& availableWorkPetes == GATHERPETES_NEEDED_GATHERING) {

						System.err.println("<---- START A MEETING!! ---->");

						System.err.println("<---- END OF MEETING!! ---->");
					} else {
						// sleep again

					}

					workPeteMeeting.acquire();

				} catch (InterruptedException e) {
				}

			}
		}

		private void regenerateEnergy() {
			try {
				System.out
						.println("The Saint is tired, and rests for a short while");
				Thread.sleep((int) (Math.random() * 10000));
				System.out
						.println("The Saint has woken up, disoriented looking if any meeting is necessary...");
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err.println("Uh-oh, Saints rest has been interrupted!");
			}
		}

		/**
		 * Verzameloverleg: Minimaal 3 werkpieten, minimaal 1 verzamelpiet
		 * beschikbaar Werkoverleg: Minimaal 3 werkpieten, GEEN verzamelpiet
		 * beschikbaar
		 */
		private void attendGathering() {

			if (canAttendGatherMeeting()) {
				// Go have a GatherMeeting
				attentGatherMeeting();
			} else if(canAttendWorkMeeting()){
				// Go have a WorkMeeting
				attendWorkMeeting();
			}
		}

		/**
		 * 
		 */
		private void attendWorkMeeting() {
			// TODO Auto-generated method stub

		}

		/**
		 * 
		 */
		private void attentGatherMeeting() {
			// TODO Auto-generated method stub

		}

		private boolean canAttendGatherMeeting() {

			return (availableGatherPetes >= GATHERPETES_NEEDED_GATHERING);
		}

		private boolean canAttendWorkMeeting() {

			return (availableWorkPetes >= WORKPETES_NEEDED_GATHERING);
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
				doWork(25);
				try {
					gatherMutex.acquire();
					// TODO Laten wachten ook al zijn er voldoende wachtende?
					if (availableGatherPetes < GATHERPETES_NEEDED_GATHERING) {
						availableGatherPetes++;
						gatherMutex.release();
					} else {
						gatherMutex.release();
					}
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

		public WorkPete(String name) {
			super(name);
		}

		@Override
		public void run() {
			while (true) {
				doWork(10);
				// Go to the Saint; check for meeting
				try {
					workMutex.acquire();
					// TODO Laten wachten ook al zijn er voldoende wachtende?
					if (availableWorkPetes < WORKPETES_NEEDED_GATHERING) {
						availableWorkPetes++;
						workMutex.release();
					} else {
						workMutex.release();
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
