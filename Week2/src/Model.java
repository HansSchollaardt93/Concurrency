
public class Model {
	private static Model model;
	
	private Model(){
		
	}
	
	public static Model getInstance(){
		if (model == null) {
			return new Model();
		}
		return model;
	}
}
