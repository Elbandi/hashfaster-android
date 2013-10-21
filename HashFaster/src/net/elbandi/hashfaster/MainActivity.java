package net.elbandi.hashfaster;

import java.sql.Date;
import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.adapters.MinerListViewAdapter;
import net.elbandi.hashfaster.controls.HomeTutorialDialog;
import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.managers.PrefManager;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.tasks.GetMinerDataTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends CustomSlidingActivity {

	TextView mUsername, mRewards, mRoundEstimate, mHashrate, mPayoutHistory, mRoundShares, mTimestamp, mLastUpdate;
	TextView mError;

	ViewPager vpWorkers;
	ListView lvWorkers;

	MinerListViewAdapter mLVAdapter;

	RefreshListener refreshListener;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSlidingActionBarEnabled(true);

		mUsername = (TextView) findViewById(R.id.tv_username);
		mRewards = (TextView) findViewById(R.id.tv_confirmed_rewards);
		mRoundEstimate = (TextView) findViewById(R.id.tv_round_estimate);
		mHashrate = (TextView) findViewById(R.id.tv_total_hashrate);
		mPayoutHistory = (TextView) findViewById(R.id.tv_payout_history);
		mRoundShares = (TextView) findViewById(R.id.tv_round_shares);
		mLastUpdate = (TextView) findViewById(R.id.tv_last_update);
		mError = (TextView) findViewById(R.id.tv_error);

		
		/*
		 * Initialize
		 */
		setUpTutorial();
		setUpSlidingDrawer();
		setUpListeners();
		updateView();
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
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			break;
		case R.id.action_refresh:
			updateView();
			break;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.action_about:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	/**
	 * Updating view with new data
	 */
	public void updateView() {
		new GetMinerDataTask(this, refreshListener).execute();
	}

	/**
	 * Setting up listeners
	 */
	private void setUpListeners() {
		refreshListener = new RefreshListener() {

			@Override
			public void onRefresh() {
				Miner mMiner = new Miner();
				mMiner = MinerManager.getInstance().miner;

				mUsername.setText(mMiner.username);
				mRewards.setText(String.valueOf(mMiner.confirmed_rewards) + " LTC");
				mRoundEstimate.setText(String.valueOf(mMiner.round_estimate) + " LTC");
				mHashrate.setText(String.valueOf(mMiner.total_hashrate) + " Kh/s");
				mPayoutHistory.setText(String.valueOf(mMiner.payout_history) + " LTC");
				mRoundShares.setText(String.valueOf(mMiner.round_shares));

				mLVAdapter.notifyDataSetChanged();

				long dtMili = System.currentTimeMillis();
				Date d = new Date(dtMili);
				CharSequence s = DateFormat.format("hh:mm:ss, EEEE, MMMM d, yyyy ", d.getTime());
				// textView is the TextView view that should display it
				mLastUpdate.setText(s);

			}
		};
	}

	private void setUpSlidingDrawer() {
		setBehindContentView(R.layout.slidingdrawer_workers);
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setFadeDegree(0.35f);

		lvWorkers = (ListView) getSlidingMenu().findViewById(R.id.lv_workers);
		mLVAdapter = new MinerListViewAdapter(this);
		lvWorkers.setAdapter(mLVAdapter);
	}
	
	private void setUpTutorial()
	{
		if (!PrefManager.getSeenHomeTutorial(this))
		{
			HomeTutorialDialog dialog = new HomeTutorialDialog(this);
			dialog.show();
		}
	}

}
