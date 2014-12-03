
public abstract class Pete  extends Thread  {
	protected String name;
	protected int id;

	public Pete(String name, int id) {
		super(name);
		this.name = name;
		this.id = id;
	}
}
