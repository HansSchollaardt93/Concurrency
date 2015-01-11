package main;

import model.Celebrity;
import model.Citizen;

public class APL {
	private final static int NR_OF_CELEBRITIES = 5;
	private final static int NR_OF_CITIZENS = 20;

	private static Thread[] celebrities;
	private static Thread[] citizens;
	
	public static void main(String[] args) {
		celebrities = new Thread[NR_OF_CELEBRITIES];
		citizens = new Thread[NR_OF_CITIZENS];
		
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
