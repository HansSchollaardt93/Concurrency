package model;

/**
 * Superclass for Citizen and Celebrity. Used for basic/shared variable management.
 * @author Hans
 *
 */
public abstract class Visitor extends Thread {
		private static int uniqueId = 0;
		private int visitorID;
	
		public Visitor() {
			this.visitorID = uniqueId++;
		}

		public int getVisitorID() {
			return visitorID;
		}

		@Override
		public String toString() {
			return "Bezoeker "+ visitorID;
		}
		
}
