public class WorkPete extends Pete {

	public WorkPete(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (true) {
			doWork(10);
			// Go to the Saint; check for meeting
		}
	}
}