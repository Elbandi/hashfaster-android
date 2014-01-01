package net.elbandi.hashfaster.managers;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.utils.StringUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

public class PoolManager {
	// private static Context mContext;
	private static HashMap<String, String> pools_url;
	private static HashMap<String, Drawable> logos;
	private static String[] poolkeys;
	private static HashMap<String, String> titles;
	private static HashMap<String, String> subtitles;

	public static void init(Context context) {
		// mContext = context;
		pools_url = StringUtils.LoadStringArray(context, R.array.pool_keys, R.array.pool_urls);
		poolkeys = context.getResources().getStringArray(R.array.pool_keys);
		logos = StringUtils.LoadDrawableArray(context, R.array.pool_keys, R.array.activity_logos);

		titles = StringUtils.LoadStringArray(context, R.array.pool_keys, R.array.activity_titles);
		subtitles = StringUtils.LoadStringArray(context, R.array.pool_keys, R.array.activity_subtitles);
	}

	public static int getPoolCount() {
		return poolkeys.length;
	}

	public static String getPoolKey(int index) {
		if (index < 0 || index >= poolkeys.length) {
			throw new IllegalArgumentException("index: " + index);
		}
		return poolkeys[index];
	}

	public static String getTitles(String key) {
		return titles.get(key);
	}

	public static String getSubTitles(String key) {
		return subtitles.get(key);
	}

	public static String getPoolUrl(String key) {
		return pools_url.get(key);
	}

	public static Drawable getLogo(String key) {
		return logos.get(key);
	}
}
