package net.elbandi.hashfaster.fragments;

import com.actionbarsherlock.app.SherlockFragment;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import net.elbandi.hashfaster.MainActivity;
import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.models.Balance;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DashboardFragment extends SherlockFragment implements RefreshListener {

	public static final String ARG_POOLID = "poolid";

	TextView mUsername, mHashrate, mRoundShares,
		mPoolHashrate, mPoolEfficiency, mPoolActiveWorkers, mPoolNextBlock, mPoolLastBlock, mPoolNetworkDiff, mPoolRoundEstimate, mPoolRoundShares, mPoolTimeLastBlock,
		mBalanceConfirmed, mBalanceUnconfirmed,
		mTimestamp;
	TextView mError;
	ScrollView mRefresh;
	int poolid;

	RefreshListener refreshListener;
	private PullToRefreshLayout mPullToRefreshLayout;

	public static DashboardFragment newInstance() {
		DashboardFragment f = new DashboardFragment();

		Bundle args = new Bundle();
		// args.putInt(ARG_POOLID, poolid);
		f.setArguments(args);

		return f;
	}

	public int getPoolId() {
		return poolid;
	}

	public void setPoolId(int poolid) {
		this.poolid = poolid;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		poolid = 0;
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

		mError = (TextView) v.findViewById(R.id.tv_error);
		mRefresh = (ScrollView) v.findViewById(R.id.information_scrollview);

		// Now give the find the PullToRefreshLayout and set it up
		mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener((MainActivity) getActivity()).setup(mPullToRefreshLayout);
		setUpListeners();
		return v;
	}

	public void setErrorLabel(int resId) {
		mError.setText(resId);
		mError.setVisibility(View.VISIBLE);
	}

	public boolean setupError(boolean error, int resId) {
		mError.setVisibility(error ? View.VISIBLE : View.GONE);
		if (error && resId > 0)
			setErrorLabel(resId);
		return error;
	}

	public PullToRefreshLayout getPullToRefreshLayout() {
		return mPullToRefreshLayout;
	}

	/**
	 * Setting up listeners
	 */
	private void setUpListeners() {
		final String seconds = getResources().getString(R.string.dateformat_seconds);
		final String minutes = getResources().getString(R.string.dateformat_minutes);
		final String hours = getResources().getString(R.string.dateformat_hours);
		final String days = getResources().getString(R.string.dateformat_days);
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
				Miner mMiner = MinerManager.getInstance().getMiner(poolid);
				Balance mBalance = mMiner.getBalance();
				Pool mPool = MinerManager.getInstance().getPool(poolid);

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
			}
		};
	}

	@Override
	public void onRefresh() {
		refreshListener.onRefresh();
	}
}
