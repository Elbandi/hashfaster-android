package net.elbandi.hashfaster.managers;

import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;

public class MinerManager {
	private static MinerManager _instance = null;

	private Miner[] miner = new Miner[8];
	private Pool[] pool = new Pool[8];

	public static MinerManager getInstance() {
		if (_instance == null)
			_instance = new MinerManager();

		return _instance;
	}

	public void setMiner(int n, Miner miner) {
		this.miner[n] = miner;
	}

	public Miner getMiner(int n) {
		if (miner[n] == null)
			miner[n] = new Miner();
		return miner[n];
	}

	public void setPool(int n, Pool pool) {
		this.pool[n] = pool;
	}

	public Pool getPool(int n) {
		if (pool[n] == null)
			pool[n] = new Pool();
		return pool[n];
	}
}
