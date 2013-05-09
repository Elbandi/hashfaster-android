package com.inajstudios.wemineltc.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inajstudios.wemineltc.MainActivity;
import com.inajstudios.wemineltc.R;
import com.inajstudios.wemineltc.managers.MinerManager;
import com.inajstudios.wemineltc.models.Miner;
import com.inajstudios.wemineltc.models.Worker;
import com.inajstudios.wemineltc.tasks.GetMinerDataTask;

public class MinerViewPagerAdapter extends PagerAdapter {

	Context mContext;

	public MinerViewPagerAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		Log.w("WEMINELTC", "Size: " + MinerManager.getInstance().miner.workers.size());
		return MinerManager.getInstance().miner.workers.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Miner mMiner = MinerManager.getInstance().miner;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Worker worker = mMiner.workers.get(position);
		View view = null;

		view = inflater.inflate(R.layout.worker_item, null);
		TextView mName = (TextView) view.findViewById(R.id.tv_worker_name);
		TextView mAlive = (TextView) view.findViewById(R.id.tv_worker_alive);
		TextView mHashRate = (TextView) view.findViewById(R.id.tv_worker_hashrate);
		TextView mTimestamp = (TextView) view.findViewById(R.id.tv_worker_lastshare_timestamp);

		mName.setText("Worker: " + worker.name);
		mAlive.setText("Alive: " + worker.alive);
		mHashRate.setText("Hashrate: " + worker.hashrate);
		if (mTimestamp != null)
			mTimestamp.setText("Last Share: " + DateFormat.format("dd/MM/yyyy hh:mm:ssaa", worker.last_share_timestamp * 1000L));

		container.addView(view);
		return view;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		
	}
}
