import java.util.concurrent.Semaphore;

public class SaintsMansion {
	private static final int NR_OF_GATHERPETES = 10;
	private static final int NR_OF_WORKPETES = 3;

	private Thread[] gatherpetes;
	private Thread[] workerpetes;

	private Semaphore gatherPeteMeeting, workPeteMeeting, workMutex,
			gatherMutex;

	public SaintsMansion() {
		gatherpetes = new Thread[NR_OF_GATHERPETES];
		workerpetes = new Thread[NR_OF_WORKPETES];

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
}
