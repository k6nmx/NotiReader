package de.ecspride.reactor.poc.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Notification;
import android.util.Log;
import de.ecspride.reactor.poc.apps.DefaultBigView;
import de.ecspride.reactor.poc.apps.DefaultTicker;
import de.ecspride.reactor.poc.apps.DefaultView;

public enum Messenger {	
	SMS("com.android.mms", new DefaultTicker()), 
	THREEMA("ch.threema.app", new DefaultView()), 
	TEXTSECURE("org.thoughtcrime.securesms", new DefaultView()), 
	HANGOUTS("com.google.android.talk",	new DefaultView()), 
	FACEBOOK("com.facebook.orca", new DefaultView()), 
	SYKPE("com.skype.raider", new DefaultView()), 
	WHATSAPP("com.whatsapp", new DefaultBigView()), 
	CHADDER("com.etransfr.chadder", new DefaultView()), 
	SAYHEY("de.simyo.app.sayhey", new DefaultView()), 
	TELEGRAM("org.telegram.messenger", new DefaultView());

	private static final String TAG = Messenger.class.getName();
	
	private String packageName;
	private MessageParser parser;

	Messenger(String packageName, MessageParser parser) {
		this.packageName = packageName.toLowerCase(Locale.ENGLISH);
		this.parser = parser;
	}

	public static Message getMessage(String packageName, Notification notification) {
		String notificationPackage = packageName.toLowerCase(
				Locale.ENGLISH);

		for (Messenger messenger : Messenger.values()) {
			if (!messenger.packageName.equals(notificationPackage))
				continue;

			Log.d(TAG, "Found matching messenger: " + messenger.packageName);

			Message result = messenger.parser.parse(notification);
			
			if (result == null)
				return null;
			
			result.app = packageName;
			result.time = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.US).format(new Date());
			
			return result;
		}

		return null;
	}
}
