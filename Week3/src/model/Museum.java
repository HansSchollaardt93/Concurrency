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
				// System.out.println("Celebrity " + visitor.getPersonId()
				// + " has arrived at the museum.");

				// assert niet teveel aan het wachten; niet meer dan dat er
				// eigenlijk bestaan!
				celebritiesWaiting++;
				System.err.println("Celebrities waiting outside: "
						+ celebritiesWaiting);
				while (celebrityVisiting || citizensInside > 0) {
					celebrityVisit.await();
					// continue
				}
				System.out.println("Celebrity " + visitor.getPersonId()
						+ " is entering the museum");

				celebrityVisiting = true;
				celebritiesWaiting--;
				celebritiesVisited++;
				System.err
						.println("Celebrities visited: " + celebritiesVisited);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		if (visitor instanceof Citizen) {
			try {
				lock.lock();
				// System.out.println("Citizen " + visitor.getPersonId()
				// + " has arrived at the museum.");
				citizensWaiting++;
				while (celebrityVisiting || isCelebritiesTurn()) {
					citizenVisit.await();
				}
				// Can continue
				citizensWaiting--;
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
		return (celebritiesVisited < 3)
				|| (citizensWaiting == 0 && celebritiesWaiting > 0);
	}

	/**
	 * 
	 * @param visitor
	 * @throws InterruptedException
	 */
	private void payVisit(Visitor visitor) throws InterruptedException {
		// simulate looking around
		takeALookAround(visitor);

		// simulate leaving of the visitor
		lock.lock();
		try {
			if (visitor instanceof Celebrity) {
				System.out.println("Celebrity " + visitor.getPersonId()
						+ " is leaving the museum \n"
						+ "Celebrities waiting: " + celebritiesWaiting
						+ "\n" + "Citizens waiting: " + citizensWaiting
						+ "\n -------------------------");
				celebrityVisiting = false;
				if (isCelebritiesTurn()) {
					celebrityVisit.signalAll();
				} else {
					// Visitors turn
					citizenVisit.signalAll();
				}
			}
			if (visitor instanceof Citizen) {
				System.out.println("Citizen " + visitor.getPersonId()
						+ " is leaving the museum. \n"
						+ "Celebrities waiting: " + celebritiesWaiting
						+ "\n" + "Citizens waiting: " + citizensWaiting
						+ "\n -------------------------");
				citizensInside--;
				if (isCelebritiesTurn()) {
					// Celebrities turn
					celebrityVisit.signalAll();
				} else {
					// Visitors turn
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
			System.out.println("Citizen " + visitor.getPersonId()
					+ " is taking a look around in the Museum");
			// Simulate visit
			Citizen.sleep((long) (Math.random()*4000));
		}
		if (visitor instanceof Celebrity) {
			System.out.println("Celebrity " + visitor.getPersonId()
					+ " is taking a look around in the Museum");
			// Simulate visit
			Celebrity.sleep((long) (Math.random()*10000));
		}
	}

}
