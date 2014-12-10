public abstract class Pete extends Thread {
	protected String name;
	private static int referenceId = 100;
	protected int id;

	public Pete(String name) {
		super(name);
		this.name = name;
		this.id = referenceId++;
	}

	public int getPeteId() {
		return id;
	}

	/**
	 * Call this method to simulate a working state of the Pete
	 * 
	 * @param maxSeconds
	 *            how long to work maximal in seconds
	 */
	public void doWork(int maxSeconds) {
		try {
			int sleep = (int) (Math.random() * (maxSeconds * 1000));
			System.out.println(this + " starting to work for maximal " + sleep);
			Thread.sleep(sleep);
			System.out.println(this + " done working for: " + sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + " ID: " + id + " with name: " + name;
	}
}