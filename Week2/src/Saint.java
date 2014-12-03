public class Saint extends Thread{
	public static boolean inMeeting = false;
	
	
	@Override
	public void run() {
		while(true){
			regenerateEnergy();
			try{
			/*
			 * Verzameloverleg:  
			 * Minimaal 3 werkpieten, minimaal 1 verzamelpiet beschikbaar
			 */
			
			//Signal pete's, invite first and send others back to work
			
			//Go have a GatherMeeting
			
			/*
			 * Werkoverleg:  
			 * Minimaal 3 werkpieten, GEEN verzamelpiet beschikbaar
			 */
			
			//Go have a WorkMeeting
			
			} catch (InterruptedException e){}
			
		}
	}

	
	public void regenerateEnergy(){
		
	}
}
