public class GatherPete extends Pete {

	public GatherPete(String name, int id) {
		super(name, id);
	}

	@Override
	public void run() {
		while (true) {
			gather();
		}
	}

	private void gather() {
		try {
			System.out.println("GatherPete nr. " + id + " met naam " + name
					+ " is gathering");
			Thread.sleep((int) (Math.random() * 5000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
