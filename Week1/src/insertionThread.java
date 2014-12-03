public class insertionThread extends Thread {
	private int array[];
	private int split;

	public insertionThread(int array[], int split) {
		this.array = array;
		this.split = split;
	}

	public void run(){
		 if (array.length > split){
		        int array1[] = new int[array.length /2];
		        int array2[] = new int[array.length - array1.length];
				for(int i = 0 ; i < array1.length ; i++){
		        	array1[i] = array[i];
		        }
				for(int i = 0 ; i < array2.length ; i++){
		        	array2[i] = array[i+array1.length];
		        }
				insertionThread temp1 = new insertionThread(array1, split);
				insertionThread temp2 = new insertionThread(array2, split);
				temp1.start();
				temp2.start();
				try{
					temp1.join(); temp2.join();
				}catch(InterruptedException e){};
				array = merge(temp1.getSorted(),temp2.getSorted());
		 }else{ 
			 for (int j = 1; j < array.length; j++) {
		            int key = array[j];
		            int i = j-1;
		            while ( (i > -1) && ( array [i] > key ) ) {
		                array [i+1] = array [i];
		                i--;
		            }
		            array[i+1] = key;
		     }
		 }
	 }

	public int[] getSorted() {
		return array;
	}
	
	public static int[] merge(int[] a, int[] b) {
	    int[] answer = new int[a.length + b.length];
	    int i = 0, j = 0, k = 0;
	    while (i < a.length && j < b.length)
	    {
	        if (a[i] < b[j])       
	            answer[k++] = a[i++];

	        else        
	            answer[k++] = b[j++];               
	    }
	    while (i < a.length)  
	        answer[k++] = a[i++];
	    while (j < b.length)    
	        answer[k++] = b[j++];
	    return answer;
	}
}
