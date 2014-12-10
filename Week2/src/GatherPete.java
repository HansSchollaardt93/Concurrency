public class GatherPete extends Pete {

	public GatherPete(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (true) {
			doWork(15);
			// Go to the Saint; check for meeting
			// gatherMutex.aqcuire();
		}
	}

}