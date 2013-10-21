package net.elbandi.hashfaster.managers;

import java.util.ArrayList;
import java.util.List;

import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Pool;
import net.elbandi.hashfaster.models.Worker;



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

	public void setWorkers(List<Worker> workers) {
		this.miner.workers = workers;
	}
	
	public void setPool(Pool pool) {
		this.miner.pool = pool;
	}
	
	public List<Worker> getWorkers()
	{
		ArrayList<Worker> result = new ArrayList<Worker>();
		
		for (Worker worker : miner.workers)
		{
			result.add(worker);
		}
		
		return result;
	}
	
}
