package de.ecspride.reactor.poc.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import de.ecspride.reactor.poc.model.Message;
import de.ecspride.reactor.poc.model.MessageStore;
import de.ecspride.reactor.poc.model.Messenger;

public class NotificationsAccessService extends AccessibilityService {
	private static final String TAG = NotificationsAccessService.class
			.getName();

	public static boolean ENABLED = false;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (!ENABLED)
			return;

		if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
			return;

		this.eventNotificationStateChanged(event);
	}

	@Override
	public void onInterrupt() {
		// do nothing
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.flags = AccessibilityServiceInfo.DEFAULT;
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

		this.setServiceInfo(info);
	}

	private void eventNotificationStateChanged(AccessibilityEvent event) {
		Notification notification = (Notification) event.getParcelableData();

		Log.d(TAG, "Incoming notification!");
		Log.d(TAG, "Ticker: " + notification.tickerText);

		Message message = Messenger.getMessage(event.getPackageName()
				.toString(), notification);

		if (message == null)
			return;

		Log.i(TAG, "Successfully parsed message:");
		Log.i(TAG, message.toString());

		MessageStore.getInstance(this).addMessage(message);
	}
}
