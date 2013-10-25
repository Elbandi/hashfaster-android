package net.elbandi.hashfaster.managers;

import net.elbandi.hashfaster.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefManager {

	private static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
	}

	/*
	 * Application's preferences
	 */

	public static String getAPIKey(Context context) {
		return getPreference(context, R.string.settings_api_key, "");
	}

	public static Boolean getSeenHomeTutorial(Context context) {
		return getPreference(context, R.string.settings_home_tutorial, false);
	}

	public static void setSeenHomeTutorial(Context context, Boolean value) {
		setPreference(context, R.string.settings_home_tutorial, value);
	}

	public static int getSyncFrequency(Context context) {
		return Integer.parseInt(getPreference(context, R.string.settings_sync_frequency, "0"));
	}

	/*
	 * Android preference helpers
	 */
	@SuppressWarnings("unused")
	private static void setPreference(Context context, String key, String value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	@SuppressWarnings("unused")
	private static void setPreference(Context context, String key, int value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	@SuppressWarnings("unused")
	private static void setPreference(Context context, String key, boolean value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	@SuppressWarnings("unused")
	private static void setPreference(Context context, int key, String value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putString(context.getString(key), value);
		editor.commit();
	}

	@SuppressWarnings("unused")
	private static void setPreference(Context context, int key, int value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putInt(context.getString(key), value);
		editor.commit();
	}

	private static void setPreference(Context context, int key, boolean value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(context.getString(key), value);
		editor.commit();
	}

	@SuppressWarnings("unused")
	private static String getPreference(Context context, String key, String defaultValue) {
		return getSharedPreferences(context).getString(key, defaultValue);
	}

	@SuppressWarnings("unused")
	private static int getPreference(Context context, String key, int defaultValue) {
		return getSharedPreferences(context).getInt(key, defaultValue);
	}

	@SuppressWarnings("unused")
	private static boolean getPreference(Context context, String key, boolean defaultValue) {
		return getSharedPreferences(context).getBoolean(key, defaultValue);
	}

	private static String getPreference(Context context, int key, String defaultValue) {
		return getSharedPreferences(context).getString(context.getString(key), defaultValue);
	}

	@SuppressWarnings("unused")
	private static int getPreference(Context context, int key, int defaultValue) {
		return getSharedPreferences(context).getInt(context.getString(key), defaultValue);
	}

	private static boolean getPreference(Context context, int key, boolean defaultValue) {
		return getSharedPreferences(context).getBoolean(context.getString(key), defaultValue);
	}
}
