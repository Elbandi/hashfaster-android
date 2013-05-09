package com.inajstudios.wemineltc.managers;

import com.inajstudios.wemineltc.models.Miner;


public class MinerManager {
	private static MinerManager _instance;

	public Miner miner = new Miner();

	public static MinerManager getInstance() {
		if (_instance == null)
			_instance = new MinerManager();

		return _instance;
	}
	
	public void setMiner(Miner miner) {
		this.miner = miner;
	}

	public Miner getMiner() {
		return miner;
	}
	
}
