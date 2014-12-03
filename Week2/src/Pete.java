
public abstract class Pete  extends Thread  {
	protected String name;
	private static int referenceId = 100;
	protected int id;

	public Pete(String name) {
		super(name);
		this.name = name;
		this.id = referenceId++;
	}
	
	public int getPeteId() {
		return id;
	}
}
