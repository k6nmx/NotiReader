package de.ecspride.reactor.poc.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
	public interface MessageListener {
		public void onMessage(Message message);
	}

	public String app = null;
	public String time = null;
	public String sender = null;
	public String message = null;

	@Override
	public String toString() {
		return "Sent from " + this.sender + ": '" + this.message + "'";
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject result = new JSONObject();

		result.put("app", this.app);
		result.put("time", this.time);
		result.put("sender", this.sender);
		result.put("message", this.message);

		return result;
	}

	public static Message fromJSON(JSONObject object) throws JSONException {
		Message result = new Message();

		result.app = object.getString("app");
		result.time = object.getString("time");
		result.sender = object.getString("sender");
		result.message = object.getString("message");

		return result;
	}
}
