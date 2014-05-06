package de.ecspride.reactor.poc.services;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import de.ecspride.reactor.poc.model.Message;
import de.ecspride.reactor.poc.model.MessageStore;
import de.ecspride.reactor.poc.model.Messenger;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationsService extends NotificationListenerService {
	private static final String TAG = NotificationsService.class
			.getName();

	public static boolean ENABLED = false;

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		if (!ENABLED)
			return;

		Log.d(TAG, "Incoming notification!");
		Log.d(TAG, "Ticker: " + sbn.getNotification().tickerText);

		Message message = Messenger.getMessage(sbn.getPackageName(),
				sbn.getNotification());

		if (message == null)
			return;

		Log.i(TAG, "Successfully parsed message:");
		Log.i(TAG, message.toString());

		MessageStore.getInstance(this).addMessage(message);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		// do nothing
	}
}
