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
public class GetMinerDataTask extends BaseDataTask {

	private GetMinerDataTask(Context context, RefreshListener listener) {
		super(context, listener);
	}

	@Override
	protected JSONObject doInBackground(String... url) {
		return DoRequest("getuserstatus");
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			if (result.length() == 0)
				return;
			MinerManager.getInstance().setMiner(MinerParser.parseMiner(result));
		} catch (JSONException e) {
			setError("Error: Invalid API Key!");
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}

}
