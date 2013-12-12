package net.elbandi.hashfaster;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.adapters.MinerListViewAdapter;
import net.elbandi.hashfaster.adapters.NavigationListAdapter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends CustomSlidingActivity implements ActionBar.OnNavigationListener {
    private PullToRefreshAttacher mPullToRefreshAttacher;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    ListView lvWorkers;

    MinerListViewAdapter mLVAdapter;
    private TypedArray pools_url;
    private TypedArray logos;
    private TypedArray apikeys;
    private TypedArray titles;
    private TypedArray subtitles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSlidingActionBarEnabled(true);

        // The attacher should always be created in the Activity's onCreate
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations, R.layout.sherlock_spinner_item);
//        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        pools_url = getResources().obtainTypedArray(R.array.pools_url);
        apikeys = getResources().obtainTypedArray(R.array.pools_key);

        Context    context   = getSupportActionBar().getThemedContext();
        logos     = getResources().obtainTypedArray(R.array.activity_logos);
        titles    = getResources().obtainTypedArray(R.array.activity_titles);
        subtitles = getResources().obtainTypedArray(R.array.activity_subtitles);
        NavigationListAdapter navigationListApdater = new NavigationListAdapter(context, logos, titles, subtitles);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(navigationListApdater, this);
        setUpSlidingDrawer();
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
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
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
        Fragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(DashboardFragment.ARG_URL, pools_url.getString(position));
        args.putString(DashboardFragment.ARG_APIKEY, apikeys.getString(position));
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        getActionBar().setIcon(logos.getDrawable(position));
        return true;
    }


    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
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

}
