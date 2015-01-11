package model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simulation class of the dutch "Rijksmuseum". Simulation of regular citizens
 * but also Celebrities visiting the Museum.
 * 
 * @author Hans Schollaardt
 *
 */
public class Museum {
	private static Museum museum;
	private final Condition celebrityVisit, citizenVisiting;
	private final Lock lock;

	private boolean celebrityVisiting;

	private int celebritiesVisited;
	private int nrOfCitizensInside;
	private int celebrities_waiting;
	private int citizens_waiting;
	private int citizensVisiting;

	/**
	 * Private constructor of the museum; takes care of setting up the
	 * variables, starting threads depending on those variables and creating
	 * Conditions and Locks.
	 * 
	 * @param nrOfCelebrities
	 *            The number of celebrity-threads used in this simulation.
	 * @param nrOfCitizens
	 *            The number of citizen-threads used in this simulation.
	 */
	private Museum() {
		lock = new ReentrantLock();
		celebrityVisit = lock.newCondition();
		citizenVisiting = lock.newCondition();
	}

	public static Museum getInstance() {
		if (museum == null) {
			museum = new Museum();
		}
		return museum;
	}

	/**
	 * 
	 * @param visitor
	 * @throws InterruptedException
	 */
	public void getTicket(Visitor visitor) throws InterruptedException {
		lock.lock();
		if (visitor instanceof Celebrity) {
			try {
				celebrities_waiting++;
				while (celebrityVisiting || nrOfCitizensInside > 0) {
					celebrityVisit.await();
					celebrityVisiting = false;
					//continue
				}
				celebrities_waiting--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (visitor instanceof Citizen) {
			try {
				citizens_waiting++;
				while (celebrityVisiting) {
					citizenVisiting.await();
					//continue
				}
				citizens_waiting--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		payVisit(visitor);
	}

	/**
	 * 
	 * @param visitor
	 * @throws InterruptedException
	 */
	private void payVisit(Visitor visitor) throws InterruptedException {
		// simulate looking around
		lock.unlock();
		takeALookAround(visitor);

		lock.lock();
		try {
			if (visitor instanceof Celebrity) {
				if (celebritiesVisited < 3) {
					celebritiesVisited++;
					letNextEnter();
				} else {
					citizenVisiting.signalAll();
					// Asured no other visitors are inside; reset all counters
					resetCounters();
				}
			}
			if (visitor instanceof Citizen) {
				letNextEnter();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 
	 * @param visitor
	 * @throws InterruptedException
	 */
	private void takeALookAround(Visitor visitor) throws InterruptedException {
		if (visitor instanceof Citizen) {
			System.out.println("Another visitor is taking a look around in the Museum");
			Citizen.sleep(5000);
		}
		if (visitor instanceof Celebrity) {
			System.out.println("Celebrity is taking a look around in the Museum");
			Celebrity.sleep(5000);
		}
	}

	private void resetCounters() {
		celebritiesVisited = 0;
		citizensVisiting = 0;
	}

	/**
	 * 
	 */
	public void letNextEnter() {
		if (celebrities_waiting > 0) {
			// signal one new celebrity to enter the Museum
			celebrityVisit.signal();
		} else {
			// Signal all the citizenthreads to attempt new entrance
			citizenVisiting.signalAll();
		}
	}

}
