package model;

public class Celebrity extends Visitor {

	public Celebrity() {
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
