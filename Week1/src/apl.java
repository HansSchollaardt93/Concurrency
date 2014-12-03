import java.util.Arrays;

public class apl {
	public static void main(String[] args) {
		System.out.println("<-------  TESTRUN: N = 25000   ------>");
		runTest(25000);			
		System.out.println("<-------  TESTRUN: N = 50000   ------>");
		runTest(50000);			
		System.out.println("<-------  TESTRUN: N = 100000   ------>");
		runTest(100000);		
		System.out.println("<-------  TESTRUN: N = 200000   ------>");
		runTest(200000);		
		System.out.println("<-------  TESTRUN: N = 400000   ------>");
		runTest(400000);		
		System.out.println("<-------  TESTRUN: N = 800000   ------>");
		runTest(800000);		
		System.out.println("<-------  TESTRUN: N = 1600000   ------>");
		runTest(1600000);		
		
	}
	
	private static void runTest(int n){
		int[] numbers = null;
		int[] tijden = new int[10];
		int split;
		for (int j = 1; j <= 30; j++) {
			split = 500 * j;
			for (int i = 0; i < 10; i++) {
				numbers = getRandomIntArray(n);
				long startTime = System.currentTimeMillis();
				numbers = insertionSort(numbers, split);
				long endTime = System.currentTimeMillis();
				long total = endTime - startTime;
				tijden[i] = (int) total;
			}

			int temp = 0;
			Arrays.sort(tijden);
			for (int k = 0; k < 8; k++) {
				temp = temp + tijden[k + 1];
			}
			temp = temp / 8;
			// printIntArray(numbers);
//			System.out.println("split : " + split);
//			System.out.println("gemiddelde =" + temp);
			System.out.println(temp);
		}
	}

	public static void printIntArray(int[] numbers) {
		for (int i : numbers) {
			System.out.println(i);
		}
	}

	public static int[] getRandomIntArray(int length) {
		int[] numbers = new int[length];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = (int) (Math.random() * length);
		}
		return numbers;
	}

	public static int[] insertionSort(int array[], int split) {
		insertionThread temp1 = new insertionThread(array, split);

		temp1.start();
		try {
			temp1.join();
		} catch (InterruptedException e) {
		}
		return temp1.getSorted();
	}

}
