package de.ecspride.reactor.poc.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.ecspride.reactor.poc.R;
import de.ecspride.reactor.poc.model.Message;
import de.ecspride.reactor.poc.model.Message.MessageListener;
import de.ecspride.reactor.poc.model.MessageStore;
import de.ecspride.reactor.poc.services.NotificationsAccessService;
import de.ecspride.reactor.poc.services.NotificationsService;

public class MainActivity extends ActionBarActivity implements MessageListener,
		OnItemClickListener {
	private static final String TAG = MainActivity.class.getName();

	private MessageStore messages = null;
	private MessageAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.main);

		this.messages = MessageStore.getInstance(this);
		this.messages.setListener(this);

		this.adapter = new MessageAdapter(this, this.messages.getMessages());

		ListView list = (ListView) this.findViewById(android.R.id.list);
		list.setAdapter(this.adapter);
		list.setEmptyView(this.findViewById(android.R.id.empty));
		list.setOnItemClickListener(this);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			NotificationsService.ENABLED = true;
		} else {
			NotificationsAccessService.ENABLED = true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		this.messages.setListener(null);

		super.onDestroy();
	}

	@Override
	public void onMessage(Message message) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MainActivity.this.adapter.notifyDataSetChanged();
			}
		});
	}

	public void btnClear_Click(MenuItem item) {
		Log.d(TAG, "Clearing all messages!");

		this.messages.clear();

		this.adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Message message = this.messages.getMessages().get(position);

		Intent intent = this.getPackageManager().getLaunchIntentForPackage(
				message.app);
		
		this.startActivity(intent);
	}

	public void btnService_Click(MenuItem item) {
		String action = "android.settings.";

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			action += "ACTION_NOTIFICATION_LISTENER_SETTINGS";
		} else {
			action += "ACCESSIBILITY_SETTINGS";
		}

		Intent intent = new Intent(action);
		this.startActivity(intent);
	}

	public static class MessageAdapter extends ArrayAdapter<Message> {
		private LayoutInflater inflater = null;
		private PackageManager packageManager = null;

		public MessageAdapter(Context context, List<Message> objects) {
			super(context, R.layout.main_item, objects);

			Log.d(TAG, "Messages: " + objects.size());

			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			this.packageManager = context.getPackageManager();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = this.getItem(position);

			View view = convertView;

			if (view == null) {
				view = this.inflater.inflate(R.layout.main_item, parent, false);
			}

			try {
				ImageView image = (ImageView) view
						.findViewById(android.R.id.icon);
				Drawable icon = this.packageManager
						.getApplicationIcon(message.app);
				image.setImageDrawable(icon);
			} catch (NameNotFoundException e) {
				Log.e(TAG, "Could not determine app icon!", e);
			}

			TextView txTitle = (TextView) view.findViewById(android.R.id.text1);
			txTitle.setText(message.sender);

			TextView txText = (TextView) view.findViewById(android.R.id.text2);
			txText.setText(message.message);

			TextView txTime = (TextView) view.findViewById(R.id.time);
			txTime.setText(message.time);

			return view;
		}
	}
}
