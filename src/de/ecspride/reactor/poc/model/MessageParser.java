package de.ecspride.reactor.poc.model;

import android.app.Notification;

public interface MessageParser {
	public Message parse(Notification notification);
}
