package net.elbandi.hashfaster;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import net.elbandi.hashfaster.network.R;
import net.elbandi.hashfaster.adapters.MainPagerAdapter;
import net.elbandi.hashfaster.adapters.MinerListViewAdapter;
import net.elbandi.hashfaster.adapters.NavigationListAdapter;
import net.elbandi.hashfaster.fragments.DashboardFragment;
import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.PoolManager;
import net.elbandi.hashfaster.managers.PrefManager;
import net.elbandi.hashfaster.tasks.GetDataTask;
import net.elbandi.hashfaster.utils.NetworkUtils;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Date;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener, OnRefreshListener {
	public static final String ARG_APIKEY = "apikey";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	LinearLayout mRefresh;
	TextView mLastUpdate;
	MenuItem refreshMenuItem;

	private DashboardFragment fragment;
	private MinerListViewAdapter mLVAdapter;

	String pool = "";
	GetDataTask dataUpdateTask = null;

	private static final String ALARM_ACTION_NAME = "net.elbandi.hashfaster.ALARM";
	ViewPager pager;
	MainPagerAdapter adapter;
	TabPageIndicator indicator;
	RefreshListener refreshListener;
	BroadcastReceiver refreshReceiver;

	PendingIntent pendingIntent;
	AlarmManager alarmManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayUseLogoEnabled(true);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// ArrayAdapter<CharSequence> list =
		// ArrayAdapter.createFromResource(context, R.array.locations,
		// R.layout.sherlock_spinner_item);
		// list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		Context context = getSupportActionBar().getThemedContext();
		NavigationListAdapter navigationListApdater = new NavigationListAdapter(context);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(navigationListApdater, this);

		mLVAdapter = new MinerListViewAdapter(this);

		adapter = new MainPagerAdapter(getSupportFragmentManager(), this, mLVAdapter);

		mLastUpdate = (TextView) findViewById(R.id.tv_last_update);
		mRefresh = (LinearLayout) findViewById(R.id.rl_refresh);

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		/*
		 * Initialize
		 */
		setUpListeners();
		setUpAlarm();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		alarmManager.cancel(pendingIntent);
	}

	@Override
	protected void onDestroy() {
		alarmManager.cancel(pendingIntent);
		unregisterReceiver(refreshReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		refreshMenuItem = menu.findItem(R.id.action_refresh);
		if (fragment != null)
			refreshMenuItem.setEnabled(fragment.getPullToRefreshLayout().isEnabled());
		else
			refreshMenuItem.setEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock
		Intent intent;
		switch (item.getItemId()) {
			case android.R.id.home :
				// ((MainActivity)getActivity()).toggle();
				break;
			case R.id.action_refresh :
				updateView(true);
				break;
			case R.id.action_settings :
				intent = new Intent(this, SettingsActivity.class);
				intent.putExtra(ARG_APIKEY, pool);
				startActivity(intent);
				break;
			case R.id.action_about :
				intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
				break;
		}
		return true;
	}

	private boolean setupError(boolean error, int resId) {
		if (refreshMenuItem != null)
			refreshMenuItem.setEnabled(!error);
		fragment.getPullToRefreshLayout().setEnabled(!error);
		fragment.setupError(error, resId);
		return error;
	}

	/**
	 * Updating view with new data
	 */
	public void updateView(boolean resetalarm) {
		if (NetworkUtils.isOn(this)) {
			if (dataUpdateTask == null || dataUpdateTask.getStatus() != Status.RUNNING) {
				fragment.getPullToRefreshLayout().setRefreshing(true);
				dataUpdateTask = new GetDataTask(this, refreshListener, pool);
				dataUpdateTask.execute();
			}
		} else {
			fragment.setErrorLabel(R.string.error_nointernet);
		}
		if (resetalarm) {
			int freq = PrefManager.getSyncFrequency(this) * 1000;
			if (freq > 0) {
				alarmManager.cancel(pendingIntent);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + freq, freq, pendingIntent);
			}
		}
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		pool = PoolManager.getPoolKey(position);
		getActionBar().setIcon(PoolManager.getLogo(pool));
		mLVAdapter.setPoolId(pool);
		mLastUpdate.setText("");
		mRefresh.setVisibility(View.GONE);
		fragment = adapter.getDashboardFragment();
		fragment.setPoolId(pool);
		if (!setupError(PrefManager.getAPIKey(this, pool).isEmpty(), R.string.error_emptykey)) {
			int freq = PrefManager.getSyncFrequency(this);
			if (freq > 0) {
				alarmManager.cancel(pendingIntent);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), freq * 1000, pendingIntent);
				// AlarmManager.ELAPSED_REALTIME_WAKEUP
				// AlarmManager.RTC
			}
		}
		return true;
	}

	@Override
	public void onRefreshStarted(View view) {
		updateView(true);
	}

	/**
	 * Setting up listeners
	 */
	private void setUpListeners() {
		final String lastupdate = getResources().getString(R.string.dateformat_lastupdate);
		refreshListener = new RefreshListener() {
			@Override
			public void onRefresh() {

				fragment.onRefresh();
				mLVAdapter.notifyDataSetChanged();

				long dtMili = System.currentTimeMillis();
				Date d = new Date(dtMili);
				CharSequence s = DateFormat.format(lastupdate, d.getTime());
				// textView is the TextView view that should display it
				mLastUpdate.setText(s);
				mRefresh.setVisibility(View.VISIBLE);

				// Notify PullToRefreshAttacher that the refresh has finished
				fragment.getPullToRefreshLayout().setRefreshComplete();
			}
		};

	}

	private void setUpAlarm() {
		refreshReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateView(false);
			}
		};
		registerReceiver(refreshReceiver, new IntentFilter(ALARM_ACTION_NAME));
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ALARM_ACTION_NAME), PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager = (AlarmManager) (getSystemService(Context.ALARM_SERVICE));
	}
}
