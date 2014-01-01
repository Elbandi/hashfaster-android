package net.elbandi.hashfaster.adapters;

import net.elbandi.hashfaster.network.R;
import net.elbandi.hashfaster.fragments.DashboardFragment;
import net.elbandi.hashfaster.fragments.WorkersListFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {
	private static final int PAGE_INDEX_DASHBOARD = 0;
	private static final int PAGE_INDEX_WORKERS = 1;

	private Context context;
	private MinerListViewAdapter mLVAdapter;
	private DashboardFragment fragment;

	public MainPagerAdapter(FragmentManager fm, Context context, MinerListViewAdapter mLVAdapter) {
		super(fm);
		this.context = context;
		this.mLVAdapter = mLVAdapter;
	}

	public DashboardFragment getDashboardFragment() {
		return fragment;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case PAGE_INDEX_DASHBOARD: {
			fragment = DashboardFragment.newInstance();
			return fragment;
		}
		case PAGE_INDEX_WORKERS:
			return new WorkersListFragment(mLVAdapter);
		default:
			throw new IllegalStateException("Illegal fragment index requested");
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case PAGE_INDEX_DASHBOARD:
			return context.getString(R.string.tab_dashboard);
		case PAGE_INDEX_WORKERS:
			return context.getString(R.string.tab_workers);
		default:
			throw new IllegalStateException("Illegal fragment index requested");
		}
	}

	@Override
	public int getCount() {
		return 2;
	}
}
