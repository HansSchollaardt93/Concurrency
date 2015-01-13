package model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.APL;

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
	private int citizensInside = 0, citizensWaiting = 0;
	private int citizensToVisitAfterCeleb;

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
	 * Method to simulate the entering of a visitor to the museum; a visitor
	 * (either way Celebrity or Citizen) will queue up and the guard
	 * (Conditions) will let the visitors in right order enter the museum
	 * 
	 * @param visitor
	 *            The visitor; Celebrity or Citizen
	 * @throws InterruptedException
	 *             exception to be thrown when a Tread stops in an unusual way;
	 *             should NOT happen though :)
	 */
	public void getTicket(Visitor visitor) throws InterruptedException {
		assert visitor != null : "Visitor cant be null.";
		assert visitor instanceof Celebrity || visitor instanceof Citizen : "Visitor has to be instanceof Celebrity or Citizen";

		if (visitor instanceof Celebrity) {
			assert (celebritiesWaiting <= APL.NR_OF_CELEBRITIES) : "There where more waiting Celebrities than Celebrities instantiated";

			try {
				lock.lock();
				// System.out.println("Celebrity " + visitor.getPersonId()
				// + " has arrived at the museum.");
				celebritiesWaiting++;
				System.err.println("Celebrities waiting outside: "
						+ celebritiesWaiting);
				// wait until everyone is out of the museum, prior to entering
				// yourself
				while (celebrityVisiting || citizensInside > 0) {
					celebrityVisit.await();
					// continue
				}

				assert (!celebrityVisiting) : "The Celebrity went inside while it was not their turn!";
				assert (celebritiesVisited < 3 || citizensInside == 0) : "The Celebrity went inside while there where 3 other Celebrities";

				System.out.println("Celebrity " + visitor.getPersonId()
						+ " is entering the museum");

				celebrityVisiting = true;
				celebritiesWaiting--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		if (visitor instanceof Citizen) {
			assert (citizensInside <= APL.NR_OF_CITIZENS) : "There where more waiting Citizens than Citizens instantiated";

			try {
				lock.lock();

				citizensWaiting++;
				while ((celebrityVisiting || isCelebritiesTurn())
						&& citizensToVisitAfterCeleb == 0) {
					citizenVisit.await();
				}

				assert (celebritiesWaiting == 0 || celebritiesVisited >= 3 || citizensToVisitAfterCeleb != 0) : "The Citizen went inside while it was not their turn!";

				// First of all, all the citizens left through in between the
				// third and fourth Celebrity will have to enter the museum
				if (citizensToVisitAfterCeleb > 0) {
					citizensToVisitAfterCeleb--;
				}
				// Can continue
				// reset the count as all people waiting for the celebrities
				// were permitted to enter
				celebritiesVisited = 0;
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
		return ((celebritiesVisited < 3 || citizensWaiting == 0)
				&& celebritiesWaiting > 0 && citizensToVisitAfterCeleb == 0);
	}

	/**
	 * Method to simulate the visit of a user in the museum, which is: looking
	 * around and afterwards leaving the museum, returning straight back to life
	 * 
	 * @param visitor
	 *            The visitor; Citizen or Celebrity
	 * @throws InterruptedException
	 *             exception to be thrown when a Tread stops in an unusual way;
	 *             should NOT happen though :)
	 */
	private void payVisit(Visitor visitor) throws InterruptedException {
		// simulate looking around
		takeALookAround(visitor);
		// Simulate leaving afterwards
		leaveMuseum(visitor);
	}

	private void leaveMuseum(Visitor visitor) {
		// simulate leaving of the visitor
		lock.lock();
		try {
			if (visitor instanceof Celebrity) {
				celebritiesVisited++;
				System.err
						.println("Celebrities visited: " + celebritiesVisited);
				System.out.println("Celebrity " + visitor.getPersonId()
						+ " is leaving the museum \n" + "Celebrities waiting: "
						+ celebritiesWaiting + "\n" + "Citizens waiting: "
						+ citizensWaiting + "\n -------------------------");
				celebrityVisiting = false;
				if (isCelebritiesTurn()) {
					celebrityVisit.signalAll();
					if (celebritiesVisited > 3 && citizensWaiting > 0) {
						celebritiesVisited = 0;
					}
				} else {
					// Visitors turn

					citizensToVisitAfterCeleb = citizensWaiting;
					citizenVisit.signalAll();
					System.err.println("After third celeb: "
							+ citizensToVisitAfterCeleb);
				}
			}
			if (visitor instanceof Citizen) {
				System.out.println("Citizen " + visitor.getPersonId()
						+ " is leaving the museum. \n"
						+ "Celebrities waiting: " + celebritiesWaiting + "\n"
						+ "Citizens waiting: " + citizensWaiting
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
	 * Method to simulate the time a user stays inside the museum, doing
	 * whatever a visitor might do in a museum
	 * 
	 * @param visitor
	 *            The visitor; Citizen or Celebrity
	 * @throws InterruptedException
	 *             exception to be thrown when a Tread stops in an unusual way;
	 *             should NOT happen though :)
	 */
	private void takeALookAround(Visitor visitor) throws InterruptedException {
		if (visitor instanceof Citizen) {
			System.out.println("Citizen " + visitor.getPersonId()
					+ " is taking a look around in the Museum");
			// Simulate visit
			Citizen.sleep((long) (Math.random() * 4000));
		}
		if (visitor instanceof Celebrity) {
			System.out.println("Celebrity " + visitor.getPersonId()
					+ " is taking a look around in the Museum");
			// Simulate visit
			Celebrity.sleep((long) (Math.random() * 10000));
		}
	}

}
