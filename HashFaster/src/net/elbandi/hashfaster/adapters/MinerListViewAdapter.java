package net.elbandi.hashfaster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.models.Worker;

public class MinerListViewAdapter extends BaseAdapter {
	Context mContext;

	public MinerListViewAdapter(Context context) {
		this.mContext = context;

	}

	@Override
	public int getCount() {
		return MinerManager.getInstance().miner.workers.size();
	}

	@Override
	public Object getItem(int position) {
		return MinerManager.getInstance().miner.workers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Worker worker = MinerManager.getInstance().miner.workers.get(position);

		convertView = inflater.inflate(R.layout.sd_worker_item, null);
		TextView mName = (TextView) convertView.findViewById(R.id.tv_worker_name);
		ImageView mAlive = (ImageView) convertView.findViewById(R.id.iv_status);
//		TextView mAlive = (TextView) convertView.findViewById(R.id.tv_worker_alive);
		TextView mHashRate = (TextView) convertView.findViewById(R.id.tv_worker_hashrate);

		mName.setText(worker.name);

		if (worker.hashrate > 0) {
			mAlive.setBackgroundResource(R.color.green);
		} else {
			mAlive.setBackgroundResource(R.color.red);
		}

		mHashRate.setText(worker.hashrate + " Kh/s");
		return convertView;
	}

}
