package main;

import model.Celebrity;
import model.Citizen;
import model.Museum;

public class APL {
	private final static int NR_OF_CELEBRITIES = 5;
	private final static int NR_OF_CITIZENS = 20;

	private static Thread[] celebrities;
	private static Thread[] citizens;
	
	public static void main(String[] args) {
		celebrities = new Thread[NR_OF_CELEBRITIES];
		citizens = new Thread[NR_OF_CITIZENS];
		
		Museum museum = new Museum();
		
		for (int i = 0; i < NR_OF_CELEBRITIES; i++) {
			celebrities[i] = new Celebrity(museum);
			celebrities[i].start();
		}
		for (int i = 0; i < NR_OF_CITIZENS; i++) {
			citizens[i] = new Citizen(museum);
			citizens[i].start();
		}
	}
}
