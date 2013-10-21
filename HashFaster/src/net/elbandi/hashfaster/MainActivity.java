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
import net.elbandi.hashfaster.tasks.GetPoolDataTask;
import net.elbandi.hashfaster.tasks.GetWorkerDataTask;

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

	TextView mUsername, mHashrate, mRoundShares,
	mPoolHashrate, mPoolEfficiency, mPoolActiveWorkers, mPoolNextBlock, mPoolLastBlock, mPoolNetworkDiff, mPoolRoundEstimate, mPoolRoundShares, mPoolTimeLastBlock, 
	mTimestamp, mLastUpdate;
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
		mHashrate = (TextView) findViewById(R.id.tv_total_hashrate);
		mRoundShares = (TextView) findViewById(R.id.tv_round_shares);

		mPoolHashrate = (TextView) findViewById(R.id.tv_pool_total_hashrate);
		mPoolEfficiency = (TextView) findViewById(R.id.tv_pool_efficiency);
		mPoolActiveWorkers = (TextView) findViewById(R.id.tv_pool_active_workers);
		mPoolNextBlock = (TextView) findViewById(R.id.tv_pool_nextnetworkblock);
		mPoolLastBlock = (TextView) findViewById(R.id.tv_pool_lastblock);
		mPoolNetworkDiff = (TextView) findViewById(R.id.tv_pool_networkdiff);
		mPoolRoundEstimate = (TextView) findViewById(R.id.tv_pool_esttime);
		mPoolRoundShares = (TextView) findViewById(R.id.tv_pool_estshares);
		mPoolTimeLastBlock = (TextView) findViewById(R.id.tv_pool_timesincelast);

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
		new GetWorkerDataTask(this, new RefreshListener() {

			@Override
			public void onRefresh() {
				mLVAdapter.notifyDataSetChanged();
			}
		}).execute();
		new GetPoolDataTask(this, refreshListener).execute();
	}

	/**
	 * Setting up listeners
	 */
	private void setUpListeners() {
		refreshListener = new RefreshListener() {

			private String FormatDate(long time) {
				long msec = time*1000;
				if ( time < 60) {
					return DateFormat.format("ss", msec).toString();
				} else if (time < 3600) { // 60*60
					return DateFormat.format("mm 'minutes' ss 'seconds", msec).toString();
				} else if (time < 86400) { // 24*60*60
					return DateFormat.format("hh 'hours' mm 'minutes' ss 'seconds", msec).toString();
				} else {
					return DateFormat.format("D 'days' hh 'hours' mm 'minutes' ss 'seconds", msec).toString();
				}
			}

			@Override
			public void onRefresh() {
				Miner mMiner = MinerManager.getInstance().miner;

				mUsername.setText(mMiner.username);
				mHashrate.setText(String.valueOf(mMiner.total_hashrate) + " Kh/s");
				mRoundShares.setText(String.valueOf(mMiner.round_shares));
				mPoolHashrate.setText(String.valueOf(mMiner.pool.hashrate) + " Kh/s");
				mPoolEfficiency.setText(String.valueOf(mMiner.pool.efficiency));
				mPoolActiveWorkers.setText(String.valueOf(mMiner.pool.workers)); 
				mPoolNextBlock.setText(String.valueOf(mMiner.pool.nextnetworkblock));
				mPoolLastBlock.setText(String.valueOf(mMiner.pool.lastblock));
				mPoolNetworkDiff.setText(String.valueOf(mMiner.pool.networkdiff));
				mPoolRoundEstimate.setText(FormatDate(Math.round(mMiner.pool.esttime)));
				mPoolRoundShares.setText(String.valueOf(Math.round(mMiner.pool.estshares)));
				mPoolTimeLastBlock.setText(FormatDate(mMiner.pool.timesincelast));

//				mLVAdapter.notifyDataSetChanged();

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
