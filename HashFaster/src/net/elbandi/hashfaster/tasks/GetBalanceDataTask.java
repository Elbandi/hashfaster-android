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
public class GetBalanceDataTask extends BaseDataTask {

	private GetBalanceDataTask(Context context, RefreshListener listener, String url, String key) {
		super(context, listener, url, key);
	}

	@Override
	protected JSONObject doInBackground(String... url) {
		return DoRequest("getuserbalance");
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			if (result.length() == 0)
				return;
			MinerManager.getInstance().getMiner().setBalance(MinerParser.parseBalance(result));
		} catch (JSONException e) {
			setError("Error: Invalid API Key!");
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}

}
