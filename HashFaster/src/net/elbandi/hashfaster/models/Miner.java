package net.elbandi.hashfaster.models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Miner {

	public String username;
	public double confirmed_rewards;
	public double round_estimate;
	public int total_hashrate;
	public double payout_history;
	public int round_shares;
	public List<Worker> workers = new ArrayList<Worker>();
	
}
