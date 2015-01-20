package model;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public abstract class Pete extends UntypedActor {
	private static int referenceId = 0;
	protected int id;
	protected ActorRef admin;

	public Pete(ActorRef admin) {
		this.admin = admin;
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
			System.out.println(this + " has started to work for "
					+ (sleep / 1000) + " seconds");
			Thread.sleep(sleep);
			System.out.println(this + " is done working");
		} catch (InterruptedException e) {
			System.err.println("Pete's work has been interrupted!");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Pete with id: " + id;
	}

}