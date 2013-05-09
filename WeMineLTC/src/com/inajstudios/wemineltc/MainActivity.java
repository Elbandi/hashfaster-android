package com.inajstudios.wemineltc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.inajstudios.wemineltc.adapters.MinerViewPagerAdapter;
import com.inajstudios.wemineltc.managers.MinerManager;
import com.inajstudios.wemineltc.managers.PrefManager;
import com.inajstudios.wemineltc.tasks.GetMinerDataTask;

public class MainActivity extends SherlockFragmentActivity {

	TextView mData;
	TextView mUsername, mRewards, mRoundEstimate, mHashrate, mPayoutHistory, mRoundShares, mTimestamp, mAddress;
	TextView mError;

	ViewPager vpWorkers;

	MinerViewPagerAdapter mAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAdapter = new MinerViewPagerAdapter(getApplicationContext());

		vpWorkers = (ViewPager) findViewById(R.id.vp_workers);
		vpWorkers.setAdapter(mAdapter);

		mError = (TextView) findViewById(R.id.tv_error);

		new GetMinerDataTask(this, mAdapter).execute();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (PrefManager.getAPIKey(this).isEmpty()) {
			mError.setVisibility(View.VISIBLE);
			mError.setText("ERROR: Empty API Key, enter one in settings!");
		} else {
			mError.setVisibility(View.GONE);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("About").setIcon(R.drawable.ic_action_help).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock

		if (item.getTitle() == "Refresh")
			new GetMinerDataTask(this, mAdapter).execute();

		if (item.getTitle() == "Settings") {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}

		if (item.getTitle() == "About") {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		return true;
	}

}
