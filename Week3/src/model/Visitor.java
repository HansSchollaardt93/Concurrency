package model;

/**
 * Superclass for Citizen and Celebrity. Used for basic/shared variable management.
 * @author Hans Schollaardt
 *
 */
public abstract class Visitor extends Thread {
		private Museum museum;
		private int id;

		public Visitor(Museum museum, int id) {
			this.museum = museum;
			this.id = id;
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
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public int getPersonId(){
			return this.id;
		}
		
		

}
