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

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.inajstudios.wemineltc.MainActivity;
import com.inajstudios.wemineltc.R;
import com.inajstudios.wemineltc.adapters.MinerViewPagerAdapter;
import com.inajstudios.wemineltc.interfaces.RefreshListener;
import com.inajstudios.wemineltc.managers.MinerManager;
import com.inajstudios.wemineltc.managers.PrefManager;
import com.inajstudios.wemineltc.models.Miner;
import com.inajstudios.wemineltc.parsers.MinerParser;

/**
 * Reads APIKEY from Shared Preferences and then attempts to get JSON from the
 * appropriate BASEURL.
 * 
 */
public class GetMinerDataTask extends AsyncTask<String, Void, JSONObject> {

	private static final String BASEURL = "http://wemineltc.com/api?api_key=";

	InputStream is = null;
	MainActivity mActivity;
	RefreshListener mListener;
	MinerViewPagerAdapter mAdapter;
	TextView mError;

	public GetMinerDataTask() {
	}

	public GetMinerDataTask(MainActivity mainActivity, RefreshListener listener) {
		mActivity = mainActivity;
		mListener = listener;
		mError = (TextView) mActivity.findViewById(R.id.tv_error);
	}

	@Override
	protected JSONObject doInBackground(String... url) {

		String JSONString = "";
		JSONObject result = new JSONObject();

		try {
			String mURL = BASEURL + PrefManager.getAPIKey(mActivity);
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
			Miner mMiner = new Miner();
			mMiner = MinerManager.getInstance().miner;

			TextView mUsername = (TextView) mActivity.findViewById(R.id.tv_username);
			TextView mRewards = (TextView) mActivity.findViewById(R.id.tv_confirmed_rewards);
			TextView mRoundEstimate = (TextView) mActivity.findViewById(R.id.tv_round_estimate);
			TextView mHashrate = (TextView) mActivity.findViewById(R.id.tv_total_hashrate);
			TextView mPayoutHistory = (TextView) mActivity.findViewById(R.id.tv_payout_history);
			TextView mRoundShares = (TextView) mActivity.findViewById(R.id.tv_round_shares);

			mUsername.setText("username: " + mMiner.username);
			mRewards.setText(String.valueOf("confirmed_rewards: " + mMiner.confirmed_rewards));
			mRoundEstimate.setText(String.valueOf("round_estimate: " + mMiner.round_estimate));
			mHashrate.setText(String.valueOf("total_hashrate: " + mMiner.total_hashrate));
			mPayoutHistory.setText(String.valueOf("payout_history: " + mMiner.payout_history));
			mRoundShares.setText(String.valueOf("round_shares: " + mMiner.round_shares));

			mListener.onRefresh();

		} catch (JSONException e) {
			setError("Error: Invalid API Key!");
			e.printStackTrace();
		}
	}

	private void setError(String error) {

	}
}
