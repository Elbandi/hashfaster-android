package net.elbandi.hashfaster;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.adapters.MinerListViewAdapter;
import net.elbandi.hashfaster.controls.HomeTutorialDialog;
import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.managers.PrefManager;
import net.elbandi.hashfaster.models.Balance;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;
import net.elbandi.hashfaster.tasks.GetDataTask;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends CustomSlidingActivity implements PullToRefreshAttacher.OnRefreshListener {
	private PullToRefreshAttacher mPullToRefreshAttacher;

	TextView mUsername, mHashrate, mRoundShares,
		mPoolHashrate, mPoolEfficiency, mPoolActiveWorkers, mPoolNextBlock, mPoolLastBlock, mPoolNetworkDiff, mPoolRoundEstimate, mPoolRoundShares, mPoolTimeLastBlock,
		mBalanceConfirmed, mBalanceUnconfirmed,
		mTimestamp, mLastUpdate;
	TextView mError;
	ScrollView mRefresh;

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
		mBalanceConfirmed = (TextView) findViewById(R.id.tv_balance_confirmed);
		mBalanceUnconfirmed = (TextView) findViewById(R.id.tv_balance_pending);

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
		mRefresh = (ScrollView) findViewById(R.id.information_scrollview);

		// Create a PullToRefreshAttacher instance
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		// Add the Refreshable View and provide the refresh listener
		mPullToRefreshAttacher.addRefreshableView(mRefresh, this);

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
			mPullToRefreshAttacher.setEnabled(false);
		} else {
			mError.setVisibility(View.GONE);
			mPullToRefreshAttacher.setEnabled(true);
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
			intent = new Intent(this, PreferenceActivity.class);
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
		if (!PrefManager.getAPIKey(this).isEmpty()) {
			mPullToRefreshAttacher.setRefreshing(true);
			new GetDataTask(this, refreshListener).execute();
		}
	}

	/**
	 * Setting up listeners
	 */
	private void setUpListeners() {
		refreshListener = new RefreshListener() {

			Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

			private String FormatDate(long time) {
				long msec = time * 1000;
				cal.setTimeInMillis(msec);
				if ( time < 60) {
					return DateFormat.format("ss", cal).toString();
				} else if (time < 3600) { // 60*60
					return DateFormat.format("mm 'minutes' ss 'seconds", cal).toString();
				} else if (time < 86400) { // 24*60*60
					return DateFormat.format("hh 'hours' mm 'minutes' ss 'seconds", cal).toString();
				} else {
					return DateFormat.format("D 'days' hh 'hours' mm 'minutes' ss 'seconds", cal).toString();
				}
			}

			@Override
			public void onRefresh() {
				Miner mMiner = MinerManager.getInstance().getMiner();
				Balance mBalance = mMiner.getBalance();
				Pool mPool = MinerManager.getInstance().getPool();

				mUsername.setText(mMiner.username);
				mHashrate.setText(String.valueOf(mMiner.total_hashrate) + " Kh/s");
				mRoundShares.setText(String.valueOf(mMiner.round_shares));
				mBalanceConfirmed.setText(String.valueOf(mBalance.confirmed));
				mBalanceUnconfirmed.setText(String.valueOf(mBalance.unconfirmed));

				mPoolHashrate.setText(String.valueOf(mPool.hashrate) + " Kh/s");
				mPoolEfficiency.setText(String.valueOf(mPool.efficiency));
				mPoolActiveWorkers.setText(String.valueOf(mPool.workers));
				mPoolNextBlock.setText(String.valueOf(mPool.nextnetworkblock));
				mPoolLastBlock.setText(String.valueOf(mPool.lastblock));
				mPoolNetworkDiff.setText(String.valueOf(mPool.networkdiff));
				mPoolRoundEstimate.setText(FormatDate(Math.round(mPool.esttime)));
				mPoolRoundShares.setText(String.valueOf(Math.round(mPool.estshares)));
				mPoolTimeLastBlock.setText(FormatDate(mPool.timesincelast));

				mLVAdapter.notifyDataSetChanged();

				long dtMili = System.currentTimeMillis();
				Date d = new Date(dtMili);
				CharSequence s = DateFormat.format("hh:mm:ss, EEEE, MMMM d, yyyy ", d.getTime());
				// textView is the TextView view that should display it
				mLastUpdate.setText(s);

				// Notify PullToRefreshAttacher that the refresh has finished
				mPullToRefreshAttacher.setRefreshComplete();
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

	@Override
	public void onRefreshStarted(View view) {
		updateView();
	}

}
