public class APL {
	public static void main(String[] args) {
		GatherPete gatherPete = new GatherPete("Gather Pete", 1);
		WorkPete workPete = new WorkPete("Worker Pete", 2);

		gatherPete.start();
		workPete.start();
	}
}
