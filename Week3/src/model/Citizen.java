package model;

public class Citizen extends Visitor {
	
	public Citizen() {
		super();
	}
	
	@Override
	public void run() {
		while(true){
			liveALife();
			visitMuseum();
		}
	}
}
