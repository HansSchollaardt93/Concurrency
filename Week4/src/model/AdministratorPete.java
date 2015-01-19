package model;

import java.util.Set;

import enums.MessageType;

public class AdministratorPete extends Pete {
	private Set<Pete> actorrefs;

	public AdministratorPete() {
		super();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MessageType) {
			MessageType received_message = (MessageType) message;
			switch (received_message) {
			case NOTIFY_AVAILABLE:
				break;

			case MAKE_BLACK:

				break;

			default:
				break;
			}
		}
	}
}
