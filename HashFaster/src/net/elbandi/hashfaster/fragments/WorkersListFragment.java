package net.elbandi.hashfaster.fragments;

import com.actionbarsherlock.app.SherlockListFragment;
import net.elbandi.hashfaster.adapters.MinerListViewAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public final class WorkersListFragment extends SherlockListFragment {

	public WorkersListFragment(MinerListViewAdapter mLVAdapter) {
		super();
		this.mLVAdapter = mLVAdapter;
	}

	private static final String KEY_CONTENT = "TestFragment:Content";

	private MinerListViewAdapter mLVAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			// mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mLVAdapter);
		setListShown(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putString(KEY_CONTENT, mContent);
	}
}
