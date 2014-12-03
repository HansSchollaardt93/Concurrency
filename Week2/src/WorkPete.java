public class WorkPete extends Pete {

	public WorkPete(String name, int id) {
		super(name, id);
	}

	@Override
	public void run() {
		while (true) {
			work();
		}
	}

	private void work() {
		try {
			System.out.println("WorkPete nr. " + id + " met naam " + name
					+ " is working");
			Thread.sleep((int) (Math.random() * 5000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
