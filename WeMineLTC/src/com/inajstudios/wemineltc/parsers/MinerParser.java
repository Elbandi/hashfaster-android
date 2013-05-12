package com.inajstudios.wemineltc.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.inajstudios.wemineltc.MainActivity;
import com.inajstudios.wemineltc.models.Miner;
import com.inajstudios.wemineltc.models.Worker;

@SuppressWarnings("unused")
public class MinerParser {

	public static Miner parseMiner(JSONObject json) throws JSONException {
		Miner result = new Miner();
		result.username = readJSONString(json, "username");
		result.confirmed_rewards = readJSONDouble(json, "confirmed_rewards");
		result.round_estimate = readJSONDouble(json, "round_estimate");
		result.total_hashrate = readJSONInt(json, "total_hashrate");
		result.payout_history = readJSONDouble(json, "payout_history");
		result.round_shares = readJSONInt(json, "round_shares");
		Log.v("WEMINELTC", "result.round_shares: " + result.round_shares);
		result.workers = parseWorkers(json.getJSONObject("workers"));
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<Worker> parseWorkers(JSONObject json) throws JSONException {
		List<Worker> result = new ArrayList<Worker>();
		Iterator<String> workerNames = json.keys();

		while (workerNames.hasNext()) {
			String workerName = workerNames.next();
			Worker worker = parseWorker(workerName, json.getJSONObject(workerName));
			Log.w("WEMINELTC", "Worker name added: " + worker.name);
			result.add(worker);
		}
		Collections.reverse(result);
		return result;
	}

	public static Worker parseWorker(String name, JSONObject json) throws JSONException {
		Worker result = new Worker();
		result.name = name;
		result.alive = readJSONInt(json, "alive");
		result.hashrate = readJSONInt(json, "hashrate");
		result.last_share_timestamp = readJSONLong(json, "last_share_timestamp");
		return result;
	}

	/*
	 * Helper Methods
	 */
	private static String readJSONString(JSONObject json, String s) throws JSONException {
		if (json.isNull(s))
			return null;
		else
			return json.getString(s);
	}

	private static Double readJSONDouble(JSONObject json, String s) throws JSONException {
		if (json.isNull(s))
			return 0.0;
		else
			return json.getDouble(s);
	}

	private static Integer readJSONInt(JSONObject json, String s) throws JSONException {
		if (json.isNull(s))
			return 0;
		else
			return json.getInt(s);
	}

	private static Long readJSONLong(JSONObject json, String s) throws JSONException {
		if (json.isNull(s))
			return (long) 0;
		else
			return json.getLong(s);
	}

	private Boolean readJSONBoolean(JSONObject json, String s) throws JSONException {
		if (json.isNull(s))
			return null;
		else
			return json.getBoolean(s);
	}

}
