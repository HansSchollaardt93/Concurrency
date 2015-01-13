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
	private final Condition celebrityVisit, citizenVisit;
	private final Lock lock;

	private boolean celebrityVisiting = false;

	private int celebritiesVisited = 0, celebritiesWaiting = 0;
	private int citizensInside = 0, citizensWaiting = 0,
			citizensRemainingTurns = 0;

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
	public Museum() {
		lock = new ReentrantLock();
		celebrityVisit = lock.newCondition();
		citizenVisit = lock.newCondition();
	}

	/**
	 * 
	 * @param visitor
	 * @throws InterruptedException
	 */
	public void getTicket(Visitor visitor) throws InterruptedException {

		if (visitor instanceof Celebrity) {
			try {
				lock.lock();
				System.out.println("Celebrity " + visitor.getPersonId()
						+ " has arrived at the museum.");

				// assert niet teveel aan het wachten; niet meer dan dat er
				// eigenlijk bestaan!
				celebritiesWaiting++;
				while (celebrityVisiting || citizensInside > 0) {
					celebrityVisit.await();
					// continue
				}
				System.out.println("Celebrity is entering the museum");
				celebrityVisiting = true;
				celebritiesWaiting--;
				celebritiesVisited++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		if (visitor instanceof Citizen) {
			try {
				lock.lock();
				System.out.println("Citizen " + visitor.getId()
						+ " has arrived at the museum.");
				citizensRemainingTurns++;
				while (celebrityVisiting || isCelebritiesTurn()) {
					citizenVisit.await();
				}
				citizensRemainingTurns--;
				citizensInside++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		payVisit(visitor);
	}

	private boolean isCelebritiesTurn() {
		return (celebritiesVisited < 3 || celebritiesWaiting == 0)
				&& citizensWaiting == 0 && celebritiesWaiting > 0;
	}

	/**
	 * 
	 * @param visitor
	 * @throws InterruptedException
	 */
	private void payVisit(Visitor visitor) throws InterruptedException {
		// simulate looking around
		takeALookAround(visitor);

		lock.lock();
		try {
			if (visitor instanceof Celebrity) {
				System.out.println("Celebrity " + visitor.getPersonId()
						+ " is leaving the museum");
				celebrityVisiting = false;
				if (celebritiesVisited == 3 && citizensRemainingTurns == 0) {
					// no citizens waiting, let another celebrity go in first
					celebritiesVisited -= 1;
					celebrityVisit.signalAll();
				} 
			}
			if (visitor instanceof Citizen) {
				System.out.println("Citizen " + visitor.getPersonId()
						+ " is leaving the museum");
				citizensInside--;
				if (isCelebritiesTurn()) {
					celebrityVisit.signalAll();
				} else {
				citizensWaiting = citizensRemainingTurns;
				citizenVisit.signalAll();
				}
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
			System.out
					.println("Another visitor is taking a look around in the Museum");
			Citizen.sleep(5000);
		}
		if (visitor instanceof Celebrity) {
			System.out
					.println("Celebrity is taking a look around in the Museum");
			Celebrity.sleep(5000);
		}
	}

	private void resetCounters() {
		celebritiesVisited = 0;
		citizensInside = 0;
	}

	/**
	 * 
	 */
	public void letNextEnter() {
		if (celebritiesWaiting > 0) {
			// signal one new celebrity to enter the Museum
			celebrityVisit.signal();
		} else {
			// Signal all the citizenthreads to attempt new entrance
			citizenVisit.signalAll();
		}
	}

}
