package net.elbandi.hashfaster.managers;

import java.util.HashMap;

import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;

public class MinerManager {
	private static MinerManager _instance = null;

	private HashMap<String, Miner> miner = new HashMap<String, Miner>();
	private HashMap<String, Pool> pool = new HashMap<String, Pool>();

	public static MinerManager getInstance() {
		if (_instance == null)
			_instance = new MinerManager();

		return _instance;
	}

	public void setMiner(String key, Miner miner) {
		this.miner.put(key, miner);
	}

	public Miner getMiner(String key) {
		if (!miner.containsKey(key)) {
			setMiner(key, new Miner());
		}
		return miner.get(key);
	}

	public void setPool(String key, Pool pool) {
		this.pool.put(key, pool);
	}

	public Pool getPool(String key) {
		if (!pool.containsKey(key)) {
			setPool(key, new Pool());
		}
		return pool.get(key);
	}
}
