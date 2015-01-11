package model;

/**
 * Superclass for Citizen and Celebrity. Used for basic/shared variable management.
 * @author Hans Schollaardt
 *
 */
public abstract class Visitor extends Thread {
		private Museum museum;
	
		public Visitor() {
			this.museum = Museum.getInstance();
		}

		public void visitMuseum(){
			try {
				museum.getTicket(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void liveALife(){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

}
