package net.elbandi.hashfaster.managers;

import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;

public class MinerManager {
	private static MinerManager _instance;

	private Miner miner = new Miner();
	private Pool pool = new Pool();

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

	public void setPool(Pool pool) {
		this.pool = pool;
	}

	public Pool getPool() {
		return pool;
	}
}
