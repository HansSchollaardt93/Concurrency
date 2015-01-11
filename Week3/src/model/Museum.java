package model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Museum {
	private final int NR_OF_CELEBRITIES;
	private final int NR_OF_CITIZENS;
	
	private Thread[] celebrities, citizens;
	private final Lock lock;
	
	private int celebritiesVisited, waitingCitizens, waitingCelebrities;
	
	private final Condition noCelebrity;
	
	
	
	
	public Museum(int nrOfCelebrities, int nrOfCitizens) {
		NR_OF_CELEBRITIES = nrOfCelebrities;
		NR_OF_CITIZENS = nrOfCitizens;
		
		lock = new ReentrantLock();
		noCelebrity = lock.newCondition();
		setUpThreads();
	
	}


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
}
