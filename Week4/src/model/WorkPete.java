package model;

import main.APL;
import enums.MessageType;

public class WorkPete extends Pete {

	public WorkPete() {
		super();
	}

	@Override
	public void preStart() {
		System.out.println("Workpete actor started");
	}

	public void postStop() {
		System.out.println("Workpete actor exiting");
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MessageType) {
			MessageType received_message = (MessageType) message;
			switch (received_message) {
			case WORK:
				doWork(APL.getMaxWorkdurationWorkpetes());
				break;

			default:
				break;
			}
		}
	}
}
