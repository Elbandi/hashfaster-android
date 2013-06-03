package com.inajstudios.wemineltc.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.inajstudios.wemineltc.adapters.MinerViewPagerAdapter;
import com.inajstudios.wemineltc.interfaces.RefreshListener;
import com.inajstudios.wemineltc.managers.MinerManager;
import com.inajstudios.wemineltc.managers.PrefManager;
import com.inajstudios.wemineltc.parsers.MinerParser;

/**
 * Reads APIKEY from Shared Preferences and then attempts to get JSON from the
 * appropriate BASEURL.
 * 
 */
public class GetMinerDataTask extends AsyncTask<String, Void, JSONObject> {

	private static final String BASEURL = "http://wemineltc.com/api?api_key=";

	InputStream is = null;
	Context mContext;
	RefreshListener mListener;
	MinerViewPagerAdapter mAdapter;

	public GetMinerDataTask(Context context, RefreshListener listener) {
		mContext = context;
		mListener = listener;
	}

	@Override
	protected JSONObject doInBackground(String... url) {

		String JSONString = "";
		JSONObject result = new JSONObject();

		try {
			String mURL = BASEURL + PrefManager.getAPIKey(mContext);
			Log.v("WEMINELTC", "GetPoolDataTask: url is + " + mURL);

			HttpPost httpPost = new HttpPost(mURL);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			setError("Error: No connection, check your internet!");
			e.printStackTrace();
		} catch (IOException e) {
			setError("Error: No connection, check your internet!");
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			JSONString = sb.toString();
			Log.v("WEMINELTC", "GetPoolDataTask: JSONString is:\n" + JSONString);
			result = new JSONObject(JSONString);
		} catch (Exception e) {
			setError("Error: Invalid API Key!");
		}

		Log.v("WEMINELTC", "GetPoolDataTask: result:\n" + result.toString());
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		try {

			new MinerParser();
			MinerManager.getInstance().setMiner(MinerParser.parseMiner(result));
			Log.w("WEMINELTC", result.toString());
			Log.w("WEMINELTC", MinerManager.getInstance().miner.username);

			if (mListener != null && result != null)
				mListener.onRefresh();

		} catch (JSONException e) {
			setError("Error: Invalid API Key!");
			e.printStackTrace();
		}
	}

	private void setError(String error) {

	}
}
