import java.util.Arrays;

public class Opdracht1 {
	private static int[] list;
	private static long start, end;
	
	public static void main(String[] args) {
		System.out.println("Tijdsduur berekening met 25000 elementen:");
		for (int i = 0; i < 10; i++) {
			calculateSingleSort(25000);
		}
		System.out.println("Tijdsduur berekening met 50000 elementen:");
		for (int i = 0; i < 10; i++) {
			calculateSingleSort(50000);
		}
		System.out.println("Tijdsduur berekening met 100000 elementen:");
		for (int i = 0; i < 10; i++) {
			calculateSingleSort(100000);
		}
		System.out.println("Tijdsduur berekening met 200000 elementen:");
		for (int i = 0; i < 10; i++) {
			calculateSingleSort(200000);
		}
		System.out.println("Tijdsduur berekening met 400000 elementen:");
		for (int i = 0; i < 10; i++) {
			calculateSingleSort(400000);
		}
		System.out.println("Tijdsduur berekening met 800000 elementen:");
		for (int i = 0; i < 10; i++) {
			calculateSingleSort(800000);
		}
		System.out.println("Tijdsduur berekening met 1600000 elementen:");
		for (int i = 0; i < 10; i++) {
			calculateSingleSort(1600000);
		}
		//printArray(list);
	}

	public static void calculateSingleSort(int elements){
		generateList(elements);
		start = System.currentTimeMillis();
		insertionSort(list);
		end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
	private static void generateList(int elementcount) {
		
		list = new int[elementcount];
		for (int j = 0; j < elementcount; j++) {
			int random = (int)(Math.random() * elementcount);
			list[j] = random;
			//System.out.println(random);
		}
		
	}
	
    private static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int valueToSort = arr[i];
            int j = i;
            while (j > 0 && arr[j - 1] > valueToSort) {
                arr[j] = arr[j - 1];
                j--;
            }
            arr[j] = valueToSort;
        }
    }
 
    public static void printArray(int[] B) {
        System.out.println(Arrays.toString(B));
    }
	
}
