package net.elbandi.hashfaster.managers;

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
		return getPreference(context, "API_KEY", "");
	}

	public static void setAPIKey(Context context, String value) {
		setPreference(context, "API_KEY", value);
	}

	public static Boolean getSeenHomeTutorial(Context context) {
		return getPreference(context, "HOME_TUTORIAL", false);
	}

	public static void setSeenHomeTutorial(Context context, Boolean value) {
		setPreference(context, "HOME_TUTORIAL", value);
	}

	/*
	 * Android preference helpers
	 */
	private static void setPreference(Context context, String key, String value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	private static void setPreference(Context context, String key, int value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private static void setPreference(Context context, String key, boolean value) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	private static String getPreference(Context context, String key, String defaultValue) {
		return getSharedPreferences(context).getString(key, defaultValue);
	}

	private static int getPreference(Context context, String key, int defaultValue) {
		return getSharedPreferences(context).getInt(key, defaultValue);
	}

	private static boolean getPreference(Context context, String key, boolean defaultValue) {
		return getSharedPreferences(context).getBoolean(key, defaultValue);
	}
}
