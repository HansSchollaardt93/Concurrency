public class GatherPete extends Pete {
	public static int availableGatherers = 0;
	
	public GatherPete(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (true) {
			gather();
			//Go to the Saint; check for meeting
			gatherMutex.aqcuire();
			
			
		}
	}

	private void gather() {
		try {
			System.out.println("GatherPete nr. " + id + " met naam " + name
					+ " is gathering");
			Thread.sleep((int) (Math.random() * 5000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
