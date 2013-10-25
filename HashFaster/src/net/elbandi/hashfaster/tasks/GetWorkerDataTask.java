package net.elbandi.hashfaster.tasks;

import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.parsers.MinerParser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * Reads APIKEY from Shared Preferences and then attempts to get JSON from the
 * appropriate BASEURL.
 * 
 */
public class GetWorkerDataTask extends BaseDataTask {

	public GetWorkerDataTask(Context context, RefreshListener listener) {
		super(context, listener);
	}

	@Override
	protected JSONObject doInBackground(String... url) {
		return DoRequest("getuserworkers");
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (result.length() == 0)
			return;
		try {
			MinerManager.getInstance().getMiner().setWorkers(MinerParser.parseWorkers(result));
		} catch (JSONException e) {
			setError("Error: Invalid API Key!");
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}
}
