package com.inajstudios.wemineltc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class WorkerFragment extends SherlockFragment {
	
	
//	View view = inflater.inflate(R.layout.worker_item, container, false);
	TextView mWorkerName;// = (TextView) view.findViewById(R.id.tv_worker_name);
	TextView mAlive;// = (TextView) view.findViewById(R.id.tv_worker_alive);
	TextView mHashrate; //= (TextView) view.findViewById(R.id.tv_worker_hashrate);
	TextView mLastTimestamp;// = (TextView) view.findViewById(R.id.tv_worker_lastshare_timestamp);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.worker_item, container, false);
		mWorkerName = (TextView) view.findViewById(R.id.tv_worker_name);
		mAlive = (TextView) view.findViewById(R.id.tv_worker_alive);
		mHashrate = (TextView) view.findViewById(R.id.tv_worker_hashrate);
		mLastTimestamp = (TextView) view.findViewById(R.id.tv_worker_lastshare_timestamp);
		return view;
	}
}
