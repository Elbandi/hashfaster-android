package net.elbandi.hashfaster.tasks;

import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.parsers.MinerParser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class GetDataTask extends BaseDataTask {

	public GetDataTask(Context context, RefreshListener listener, String key) {
		super(context, listener, key);
	}

	@Override
	protected JSONObject doInBackground(String... url) {
		JSONObject result = new JSONObject();
		try {
			result.put("getuserstatus", DoRequest("getuserstatus").opt("getuserstatus"));
			result.put("getuserworkers", DoRequest("getuserworkers").opt("getuserworkers"));
			result.put("getpoolstatus", DoRequest("getpoolstatus").opt("getpoolstatus"));
			result.put("getuserbalance", DoRequest("getuserbalance").opt("getuserbalance"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			if (result.length() != 0) {
				Miner miner = MinerParser.parseMiner(result);
				miner.setWorkers(MinerParser.parseWorkers(result));
				miner.setBalance(MinerParser.parseBalance(result));
				MinerManager.getInstance().setMiner(mKey, miner);
				MinerManager.getInstance().setPool(mKey, MinerParser.parsePool(result));
			}
		} catch (JSONException e) {
			setError("Error: Invalid API Key!");
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}
}
