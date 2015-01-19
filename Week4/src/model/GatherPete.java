package model;

public class GatherPete extends Pete {

	public GatherPete() {
		super();
	}

	@Override
	public void preStart() {
		System.out.println("Gatherpete actor started");
	}

	public void postStop() {
		System.out.println("Gatherpete actor exiting");
	}

	@Override
	public void onReceive(Object message) throws Exception {

	}

}
