package net.elbandi.hashfaster.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.elbandi.hashfaster.MainActivity;
import net.elbandi.hashfaster.models.Balance;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;
import net.elbandi.hashfaster.models.Worker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


@SuppressWarnings("unused")
public class MinerParser {

	public static Miner parseMiner(JSONObject json) throws JSONException {
		Miner result = new Miner();
		if (json.has("getuserstatus")) {
			json = json.getJSONObject("getuserstatus");
			result.username = readJSONString(json, "username");
			result.total_hashrate = readJSONInt(json, "hashrate");
			result.round_shares = !json.isNull("shares") ? readJSONInt(json.getJSONObject("shares"), "valid") : 0;
			result.round_shares_invalid = !json.isNull("shares") ? readJSONInt(json.getJSONObject("shares"), "invalid") : 0;
		}
		return result;
	}

	public static Pool parsePool(JSONObject json) throws JSONException {
		Pool pool = new Pool();
		if (json.has("getpoolstatus")) {
			json = json.getJSONObject("getpoolstatus");
			pool.hashrate =  readJSONInt(json, "hashrate");
			pool.workers =  readJSONInt(json, "workers");
			pool.efficiency =  readJSONDouble(json, "efficiency");
			pool.currentnetworkblock =  readJSONInt(json, "currentnetworkblock");
			pool.nextnetworkblock =  readJSONInt(json, "nextnetworkblock");
			pool.lastblock =  readJSONInt(json, "lastblock");
			pool.networkdiff =  readJSONDouble(json, "networkdiff");
			pool.esttime =  readJSONDouble(json, "esttime");
			pool.estshares =  readJSONDouble(json, "estshares");
			pool.timesincelast =  readJSONInt(json, "timesincelast");
		}
		return pool;
	}
	
	public static List<Worker> parseWorkers(JSONObject json) throws JSONException {
		List<Worker> result = new ArrayList<Worker>();
		if (json.has("getuserworkers")) {
			JSONArray workers = json.getJSONArray("getuserworkers");

			for (int i = 0; i < workers.length(); i++) {
				Worker worker = parseWorker(workers.getJSONObject(i));
				result.add(worker);
			}
			Collections.reverse(result);
		}
		return result;
	}

	public static Worker parseWorker(JSONObject json) throws JSONException {
		Worker result = new Worker();
		result.id = readJSONInt(json, "id");
		result.name = readJSONString(json, "username");
		result.monitor = readJSONInt(json, "monitor");
		result.hashrate = readJSONInt(json, "hashrate");
		result.difficulty = readJSONInt(json, "difficulty");
		return result;
	}

	public static Balance parseBalance(JSONObject json) throws JSONException {
		Balance result = new Balance();
		if (json.has("getuserbalance")) {
			json = json.getJSONObject("getuserbalance");
			result.confirmed =  readJSONDouble(json, "confirmed");
			result.unconfirmed =  readJSONDouble(json, "unconfirmed");
			result.orphaned =  readJSONDouble(json, "orphaned");
		}
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
