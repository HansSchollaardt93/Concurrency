package model;

public class Citizen extends Visitor {
	private static int uniqueId = 0;

	public Citizen(Museum museum) {
		super(museum, uniqueId++);
	}

	@Override
	public void run() {
		while (true) {
			visitMuseum();
			liveALife();
		}
	}
}
