package net.elbandi.hashfaster.widget;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.WidgetConfigurationActivity;
import net.elbandi.hashfaster.managers.PrefManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	private static final String TAG = MyWidgetProvider.class.getName();
	// update rate in milliseconds
	public static final int UPDATE_RATE = 10000;
	static AlarmManager alarms;

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i(TAG, "onDeleted called");
		for (int appWidgetId : appWidgetIds) {
			Log.i(TAG, "deleting: " + appWidgetId);
			setAlarm(context, appWidgetId, -1);
			PrefManager.delWidgetPoolId(context, appWidgetId);
		}
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		Log.i(TAG, "onEnabled called");
		alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		Log.i(TAG, "onDisabled called");
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		int[] widgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, MyWidgetProvider.class));
		onDeleted(context, widgetIds);
		context.stopService(new Intent(context, UpdateWidgetService.class));
		super.onDisabled(context);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(TAG, "onUpdate called");
		for (int appWidgetId : appWidgetIds) {
			int poolid = PrefManager.getWidgetPoolId(context, appWidgetId);
			int syncfreq = PrefManager.getWidgetSyncFrequency(context, appWidgetId) * 1000;
			Log.i(TAG, "onUpdate " + appWidgetId + " : " + poolid);
			if (poolid != -1) {
				setAlarm(context, appWidgetId, syncfreq);
				RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
				TypedArray logos = context.getResources().obtainTypedArray(R.array.activity_logos);
				try {
					BitmapDrawable bd = (BitmapDrawable) logos.getDrawable(poolid);
					Bitmap b = bd.getBitmap();
					widget.setImageViewBitmap(R.id.widget_icon, b);
				} finally {
					logos.recycle();
				}

				widget.setOnClickPendingIntent(R.id.widget_configure, MyWidgetProvider.makeConfigurePendingIntent(context, appWidgetId));
				widget.setOnClickPendingIntent(R.id.widget, MyWidgetProvider.makeControlPendingIntent(context, UpdateWidgetService.UPDATE, appWidgetId));
				appWidgetManager.updateAppWidget(appWidgetId, widget);
			}
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public static void setAlarm(Context context, int appWidgetId, int updateRate) {
		alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent newPending = makeControlPendingIntent(context, UpdateWidgetService.UPDATE, appWidgetId);
		alarms.cancel(newPending);
		if (updateRate > 0) {
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRate, newPending);
		} else if (updateRate == 0) {
			alarms.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), newPending);
		} else {
			// on a negative updateRate stop the refreshing
		}
	}

	public static PendingIntent makeControlPendingIntent(Context context, String command, int appWidgetId) {
		context.stopService(new Intent(context, UpdateWidgetService.class));
		Intent active = new Intent(context, UpdateWidgetService.class);
		active.setAction(command);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// this Uri data is to make the PendingIntent unique, so it wont be
		// updated by FLAG_UPDATE_CURRENT
		// so if there are multiple widget instances they wont override each
		// other
		Uri data = Uri.withAppendedPath(Uri.parse("hashfasterwidget://widget/id/#" + command + appWidgetId), String.valueOf(appWidgetId));
		active.setData(data);
		return (PendingIntent.getService(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
	}

	public static PendingIntent makeConfigurePendingIntent(Context context, int appWidgetId) {
		Intent active = new Intent(context, WidgetConfigurationActivity.class);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		active.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		active.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// this Uri data is to make the PendingIntent unique, so it wont be
		// updated by FLAG_UPDATE_CURRENT
		// so if there are multiple widget instances they wont override each
		// other
		Uri data = Uri.withAppendedPath(Uri.parse("hashfasterwidget://configurewidget/id/#" + appWidgetId), String.valueOf(appWidgetId));
		active.setData(data);
		return (PendingIntent.getActivity(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
	}

	/*
	 * @Override public void onUpdate(Context context, AppWidgetManager
	 * appWidgetManager, int[] appWidgetIds) {
	 * 
	 * // Get all ids ComponentName thisWidget = new ComponentName(context,
	 * MyWidgetProvider.class); int[] allWidgetIds =
	 * appWidgetManager.getAppWidgetIds(thisWidget);
	 * 
	 * // Build the intent to call the service Intent intent = new
	 * Intent(context, UpdateWidgetService.class);
	 * intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
	 * 
	 * // Update the widgets via the service context.startService(intent); }
	 */
}
