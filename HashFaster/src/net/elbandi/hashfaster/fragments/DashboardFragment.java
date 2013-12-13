package net.elbandi.hashfaster.fragments;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import net.elbandi.hashfaster.AboutActivity;
import net.elbandi.hashfaster.MainActivity;
import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.SettingsActivity;
import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.managers.PrefManager;
import net.elbandi.hashfaster.models.Balance;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;
import net.elbandi.hashfaster.tasks.GetDataTask;
import net.elbandi.hashfaster.utils.NetworkUtils;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DashboardFragment extends SherlockFragment implements PullToRefreshAttacher.OnRefreshListener {
    public static final String ARG_URL = "url";
    public static final String ARG_APIKEY = "apikey";
    private PullToRefreshAttacher mPullToRefreshAttacher;
    
    String url;
    String apikey;
    TextView mUsername, mHashrate, mRoundShares,
        mPoolHashrate, mPoolEfficiency, mPoolActiveWorkers, mPoolNextBlock, mPoolLastBlock, mPoolNetworkDiff, mPoolRoundEstimate, mPoolRoundShares, mPoolTimeLastBlock,
        mBalanceConfirmed, mBalanceUnconfirmed,
        mTimestamp, mLastUpdate;
    TextView mError;
    ScrollView mRefresh;

    ViewPager vpWorkers;

    GetDataTask dataUpdateTask = null;
    RefreshListener refreshListener;

    private static final String ALARM_ACTION_NAME = "net.elbandi.hashfaster.ALARM";
    BroadcastReceiver refreshReceiver;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    MenuItem refreshMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        url = getArguments().getString(ARG_URL);
        apikey = getArguments().getString(ARG_APIKEY);
    }
    
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false); 

        mUsername = (TextView) v.findViewById(R.id.tv_username);
        mHashrate = (TextView) v.findViewById(R.id.tv_total_hashrate);
        mRoundShares = (TextView) v.findViewById(R.id.tv_round_shares);
        mBalanceConfirmed = (TextView) v.findViewById(R.id.tv_balance_confirmed);
        mBalanceUnconfirmed = (TextView) v.findViewById(R.id.tv_balance_pending);

        mPoolHashrate = (TextView) v.findViewById(R.id.tv_pool_total_hashrate);
        mPoolEfficiency = (TextView) v.findViewById(R.id.tv_pool_efficiency);
        mPoolActiveWorkers = (TextView) v.findViewById(R.id.tv_pool_active_workers);
        mPoolNextBlock = (TextView) v.findViewById(R.id.tv_pool_nextnetworkblock);
        mPoolLastBlock = (TextView) v.findViewById(R.id.tv_pool_lastblock);
        mPoolNetworkDiff = (TextView) v.findViewById(R.id.tv_pool_networkdiff);
        mPoolRoundEstimate = (TextView) v.findViewById(R.id.tv_pool_esttime);
        mPoolRoundShares = (TextView) v.findViewById(R.id.tv_pool_estshares);
        mPoolTimeLastBlock = (TextView) v.findViewById(R.id.tv_pool_timesincelast);

        mLastUpdate = (TextView) v.findViewById(R.id.tv_last_update);
        mError = (TextView) v.findViewById(R.id.tv_error);
        mRefresh = (ScrollView) v.findViewById(R.id.information_scrollview);

        // Now get the PullToRefresh attacher from the Activity. An exercise to the reader
        // is to create an implicit interface instead of casting to the concrete Activity
        mPullToRefreshAttacher = ((MainActivity) getActivity())
                .getPullToRefreshAttacher();
        // Add the Refreshable View and provide the refresh listener
        mPullToRefreshAttacher.addRefreshableView(mRefresh, this);

        /*
         * Initialize
         */
        setUpListeners();
        setUpAlarm();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!setupError(PrefManager.getAPIKey(getActivity(), apikey).isEmpty(), R.string.error_emptykey)) {
            int freq = PrefManager.getSyncFrequency(getActivity());
            if (freq > 0) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), freq * 1000, pendingIntent);
                // AlarmManager.ELAPSED_REALTIME_WAKEUP
                // AlarmManager.RTC
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onDestroy() {
        alarmManager.cancel(pendingIntent);
        getActivity().unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_home, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);
        refreshMenuItem.setEnabled(mPullToRefreshAttacher.isEnabled());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This uses the imported MenuItem from ActionBarSherlock
        Intent intent;
        switch (item.getItemId()) {
        case android.R.id.home:
            //((MainActivity)getActivity()).toggle();
            break;
        case R.id.action_refresh:
            updateView(true);
            break;
        case R.id.action_settings:
            intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra(ARG_APIKEY, apikey);
            startActivity(intent);
            break;
        case R.id.action_about:
            intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
            break;
        }
        return true;
    }

    private void setErrorLabel(int resId) {
        mError.setText(resId);
        mError.setVisibility(View.VISIBLE);
    }

    private boolean setupError(boolean error, int resId) {
        if (refreshMenuItem != null)
            refreshMenuItem.setEnabled(!error);
        mPullToRefreshAttacher.setEnabled(!error);
        mError.setVisibility(error ? View.VISIBLE : View.GONE);
        if (error && resId > 0)
            setErrorLabel(resId);
        return error;
    }

    /**
     * Updating view with new data
     */
    public void updateView(boolean resetalarm) {
        if (NetworkUtils.isOn(getActivity())) {
            if (dataUpdateTask == null || dataUpdateTask.getStatus() != Status.RUNNING) {
                mPullToRefreshAttacher.setRefreshing(true);
                dataUpdateTask = new GetDataTask(getActivity(), refreshListener, url, apikey);
                dataUpdateTask.execute();
            }
        } else {
            setErrorLabel(R.string.error_nointernet);
        }
        if (resetalarm) {
            int freq = PrefManager.getSyncFrequency(getActivity()) * 1000;
            if (freq > 0) {
                alarmManager.cancel(pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + freq, freq, pendingIntent);
            }
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        updateView(true);
    }

    /**
     * Setting up listeners
     */
    private void setUpListeners() {
        final String seconds = getResources().getString(R.string.dateformat_seconds);
        final String minutes = getResources().getString(R.string.dateformat_minutes);
        final String hours = getResources().getString(R.string.dateformat_hours);
        final String days = getResources().getString(R.string.dateformat_days);
        final String lastupdate = getResources().getString(R.string.dateformat_lastupdate);
        final MainActivity ma = (MainActivity)getActivity();
        refreshListener = new RefreshListener() {

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

            private String FormatDate(long time) {
                long msec = time * 1000;
                cal.setTimeInMillis(msec);
                if (time < 60) {
                    return DateFormat.format(seconds, cal).toString();
                } else if (time < 3600) { // 60*60
                    return DateFormat.format(minutes, cal).toString();
                } else if (time < 86400) { // 24*60*60
                    return DateFormat.format(hours, cal).toString();
                } else {
                    return DateFormat.format(days, cal).toString();
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

                ma.getWorkersListViewAdapter().notifyDataSetChanged();

                long dtMili = System.currentTimeMillis();
                Date d = new Date(dtMili);
                CharSequence s = DateFormat.format(lastupdate, d.getTime());
                // textView is the TextView view that should display it
                mLastUpdate.setText(s);

                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshAttacher.setRefreshComplete();
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
        getActivity().registerReceiver(refreshReceiver, new IntentFilter(ALARM_ACTION_NAME));
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ALARM_ACTION_NAME), PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) (getActivity().getSystemService(Context.ALARM_SERVICE));
    }
}
