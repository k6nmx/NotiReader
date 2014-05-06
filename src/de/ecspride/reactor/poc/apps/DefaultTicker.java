package de.ecspride.reactor.poc.apps;

import android.app.Notification;
import de.ecspride.reactor.poc.model.Message;
import de.ecspride.reactor.poc.model.MessageParser;

public class DefaultTicker implements MessageParser {
	@Override
	public Message parse(Notification notification) {
		Message result = new Message();

		String ticker = notification.tickerText.toString();

		int indexDelimiter = ticker.indexOf(':');

		if (indexDelimiter == -1)
			return null;

		result.sender = ticker.substring(0, indexDelimiter);
		result.message = ticker.substring(indexDelimiter + 2);

		return result;
	}
}
