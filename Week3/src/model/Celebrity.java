package model;

public class Celebrity extends Visitor {
	private static int uniqueId = 0;
	
	public Celebrity(Museum museum) {
		super(museum, uniqueId++);
	}
	
	@Override
	public void run() {
		while(true){
			visitMuseum();
			liveALife();
		}
	}
	

}
