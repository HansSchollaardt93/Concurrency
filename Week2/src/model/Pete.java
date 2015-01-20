package model;
public abstract class Pete extends Thread {
	protected String name;
	private static int referenceId = 0;
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
	 *            how long to work at maximum in seconds
	 */
	public void doWork(int maxSeconds) {
		try {
			int sleep = (int) (Math.random() * (maxSeconds * 1000));
			System.out.println(this + " has started to work for " + (sleep/1000) + " seconds");
			Thread.sleep(sleep);
			System.out.println(this + " is done working");
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Pete's work has been interrupted!");
		}
	}

	@Override
	public String toString() {
		return  "Pete with id: " + name;
	}
	
	
}