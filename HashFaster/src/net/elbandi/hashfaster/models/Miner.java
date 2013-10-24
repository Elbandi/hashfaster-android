package net.elbandi.hashfaster.models;

import java.util.ArrayList;
import java.util.List;

public class Miner {

	public String username;
	public int total_hashrate; // hashrate
	public int round_shares; // shares.valid
	public int round_shares_invalid; // shares.invalid
	private List<Worker> workers = new ArrayList<Worker>();

	public List<Worker> getWorkers() {
		return workers;
	}
	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}
}
