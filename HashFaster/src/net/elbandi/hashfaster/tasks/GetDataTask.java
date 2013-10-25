package net.elbandi.hashfaster.tasks;

import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.parsers.MinerParser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class GetDataTask extends BaseDataTask {

	public GetDataTask(Context context, RefreshListener listener) {
		super(context, listener);
	}

	@Override
	protected JSONObject doInBackground(String... url) {
		JSONObject result = new JSONObject();
		try {
			result.put("getuserstatus", DoRequest("getuserstatus").optJSONObject("getuserstatus"));
			result.put("getuserworkers", DoRequest("getuserworkers").optJSONObject("getuserworkers"));
			result.put("getpoolstatus", DoRequest("getpoolstatus").optJSONObject("getpoolstatus"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			if (result.length() == 0)
				return;
			MinerManager manager = MinerManager.getInstance();
			Miner miner = MinerParser.parseMiner(result);
			miner.setWorkers(MinerParser.parseWorkers(result));
			manager.setMiner(miner);
			manager.setPool(MinerParser.parsePool(result));
		} catch (JSONException e) {
			setError("Error: Invalid API Key!");
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}
}
