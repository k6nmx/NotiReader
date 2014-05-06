package de.ecspride.reactor.poc.apps;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import de.ecspride.reactor.poc.model.Message;
import de.ecspride.reactor.poc.model.MessageParser;

@SuppressLint("NewApi")
public class DefaultBigView implements MessageParser {
	private static final String TAG = DefaultBigView.class.getName();

	private static final int ID_FIRST_LINE = 16909023; // bigContentView

	@Override
	public Message parse(Notification notification) {
		// use simple method if bigContentView is not supported
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			return new DefaultView().parse(notification);

		Message result = new Message();

		try {
			RemoteViews views = notification.bigContentView;

			Class<?> rvClass = views.getClass();

			Field field = rvClass.getDeclaredField("mActions");
			field.setAccessible(true);

			@SuppressWarnings("unchecked")
			ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field
					.get(views);

			for (Parcelable action : actions) {
				try {
					// create parcel from action
					Parcel parcel = Parcel.obtain();
					action.writeToParcel(parcel, 0);
					parcel.setDataPosition(0);

					// check if is 2 / ReflectionAction
					int tag = parcel.readInt();
					if (tag != 2)
						continue;

					int viewId = parcel.readInt();

					String methodName = parcel.readString();
					if (methodName == null || !methodName.equals("setText")) {
						Log.w(TAG, "# Not setText: " + methodName);
						continue;
					}

					// should be 10 / Character Sequence, here
					parcel.readInt();

					// Store the actual string
					String value = TextUtils.CHAR_SEQUENCE_CREATOR
							.createFromParcel(parcel).toString();

					Log.d(TAG, "viewId is " + viewId);
					Log.d(TAG, "Found value: " + value);

					if (viewId == ID_FIRST_LINE) {
						int indexDelimiter = value.indexOf(':');

						if (indexDelimiter != -1) {
							result.sender = value.substring(0, indexDelimiter);
							result.message = value
									.substring(indexDelimiter + 2);
						}
					}

					parcel.recycle();
				} catch (Exception e) {
					Log.e(TAG, "Error accessing object!", e);
				}
			}

			if (result.sender == null || result.message == null)
				return null;

			return result;
		} catch (Exception e) {
			Log.e(TAG, "Could not access mActions!", e);

			return null;
		}
	}
}
