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
	private final int NR_OF_CELEBRITIES;
	private final int NR_OF_CITIZENS;

	private Thread[] celebrities, citizens;
	private final Condition noCelebrity;
	private final Lock lock;

	private int celebritiesVisited, waitingCitizens, waitingCelebrities;

	private boolean celebrityVisiting;

	/**
	 * Public constructor of the museum; takes care of setting up the variables,
	 * starting threads depending on those variables and creating Conditions and
	 * Locks.
	 * 
	 * @param nrOfCelebrities
	 *            The number of celebrity-threads used in this simulation.
	 * @param nrOfCitizens
	 *            The number of citizen-threads used in this simulation.
	 */
	public Museum(int nrOfCelebrities, int nrOfCitizens) {
		NR_OF_CELEBRITIES = nrOfCelebrities;
		NR_OF_CITIZENS = nrOfCitizens;

		celebrities = new Thread[NR_OF_CELEBRITIES];
		citizens = new Thread[NR_OF_CITIZENS];

		lock = new ReentrantLock();
		noCelebrity = lock.newCondition();
		setUpThreads();
		
		lock.

	}

	/**
	 * Method for setting up and starting the Threads used in this simulation
	 */
	private void setUpThreads() {
		for (int i = 0; i < NR_OF_CELEBRITIES; i++) {
			celebrities[i] = new Celebrity();
			celebrities[i].start();
		}
		for (int i = 0; i < NR_OF_CITIZENS; i++) {
			citizens[i] = new Citizen();
			citizens[i].start();
		}

	}

	public class Citizen extends Visitor {
		boolean isWaiting;
		private Museum museum;

		public Citizen() {
			super();

			while (!isWaiting) {
				try {

				} finally {
					lock.unlock();
				}
			}
		}

	}

	public class Celebrity extends Visitor {

	}
}
