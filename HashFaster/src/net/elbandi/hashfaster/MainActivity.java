package net.elbandi.hashfaster;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.adapters.MinerListViewAdapter;
import net.elbandi.hashfaster.adapters.NavigationListAdapter;
import net.elbandi.hashfaster.fragments.DashboardFragment;
import net.elbandi.hashfaster.fragments.WorkersListFragment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.TabPageIndicator;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {
	private static final int PAGE_INDEX_DASHBOARD = 0;
	private static final int PAGE_INDEX_WORKERS = 1;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	ListView lvWorkers;

	private MinerListViewAdapter mLVAdapter;
	private TypedArray pools_url;
	private TypedArray logos;
	private TypedArray apikeys;
	private TypedArray titles;
	private TypedArray subtitles;

	ViewPager pager;
	MainPagerAdapter adapter;
	TabPageIndicator indicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// The attacher should always be created in the Activity's onCreate
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

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

		pools_url = getResources().obtainTypedArray(R.array.pools_url);
		apikeys = getResources().obtainTypedArray(R.array.pools_key);

		Context context = getSupportActionBar().getThemedContext();
		logos = getResources().obtainTypedArray(R.array.activity_logos);
		titles = getResources().obtainTypedArray(R.array.activity_titles);
		subtitles = getResources().obtainTypedArray(R.array.activity_subtitles);
		NavigationListAdapter navigationListApdater = new NavigationListAdapter(context, logos, titles, subtitles);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(navigationListApdater, this);

		mLVAdapter = new MinerListViewAdapter(this);

		adapter = new MainPagerAdapter(getSupportFragmentManager(), 0);

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	@Override
	protected void onDestroy() {
		pools_url.recycle();
		apikeys.recycle();
		logos.recycle();
		titles.recycle();
		subtitles.recycle();
		super.onDestroy();
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
		getActionBar().setIcon(logos.getDrawable(position));
		if (adapter.getPoolId() != position) {
			adapter = new MainPagerAdapter(getSupportFragmentManager(), position);
			pager.setAdapter(adapter);
		}
		return true;
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	public MinerListViewAdapter getWorkersListViewAdapter() {
		return mLVAdapter;
	}

	class MainPagerAdapter extends FragmentPagerAdapter {
		private FragmentManager fm;
		private int poolId;
		public MainPagerAdapter(FragmentManager fm, int poolId) {
			super(fm);
			this.fm = fm;
			this.poolId = poolId;
		}

		public int getPoolId() {
			return poolId;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case PAGE_INDEX_DASHBOARD : {
					Fragment fragment = new DashboardFragment();
					Bundle args = new Bundle();
					args.putString(DashboardFragment.ARG_URL, pools_url.getString(poolId));
					args.putString(DashboardFragment.ARG_APIKEY, apikeys.getString(poolId));
					fragment.setArguments(args);
					return fragment;
				}
				case PAGE_INDEX_WORKERS :
					return new WorkersListFragment(mLVAdapter);
				default :
					throw new IllegalStateException("Illegal fragment index requested");
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			FragmentTransaction bt = fm.beginTransaction();
			bt.remove((Fragment) object);
			bt.commit();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case PAGE_INDEX_DASHBOARD :
					return getApplicationContext().getString(R.string.tab_dashboard);
				case PAGE_INDEX_WORKERS :
					return getApplicationContext().getString(R.string.tab_workers);
				default :
					throw new IllegalStateException("Illegal fragment index requested");
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
