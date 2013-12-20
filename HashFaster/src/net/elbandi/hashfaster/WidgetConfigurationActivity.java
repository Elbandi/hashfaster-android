package net.elbandi.hashfaster;

import net.elbandi.hashfaster.managers.PrefManager;
import net.elbandi.hashfaster.widget.MyWidgetProvider;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WidgetConfigurationActivity extends PreferenceActivity {
	private int appWidgetId;
	private TypedArray titles;
	private TypedArray syncFrequency;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.widget_preferences);
		// get the appWidgetId of the appWidget being configured
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		// set the result for cancel first
		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);
		LayoutInflater inflater = LayoutInflater.from(this);

		titles = getResources().obtainTypedArray(R.array.activity_titles);
		syncFrequency = getResources().obtainTypedArray(R.array.syncFrequency);

		final ListPreference list_pool_id = (ListPreference) findPreference(getString(R.string.settings_pool_id));
		final ListPreference list_syncfrequency = (ListPreference) findPreference(getString(R.string.settings_pool_sync_frequency));
		int pool_id = PrefManager.getWidgetPoolId(this, appWidgetId);
		int sync_freq = PrefManager.getWidgetSyncFrequency(this, appWidgetId);

		list_pool_id.setDefaultValue(pool_id);
		list_pool_id.setValue(Integer.toString(pool_id));
		list_pool_id.setSummary(titles.getString(pool_id));
		list_pool_id.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				list_pool_id.setSummary(titles.getString(Integer.parseInt((String) newValue)));
				return true;
			}
		});
		list_syncfrequency.setDefaultValue(sync_freq);
		list_syncfrequency.setValue(Integer.toString(sync_freq));
		list_syncfrequency.setSummary(syncFrequency.getString(pool_id));
		list_syncfrequency.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				list_syncfrequency.setSummary(syncFrequency.getString(Integer.parseInt((String) newValue)));
				return true;
			}
		});

		PreferenceManager.setDefaultValues(this, R.xml.widget_preferences, false);
		Button btn = (Button) inflater.inflate(R.layout.buttonbarbutton, null, false);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// get the date from DatePicker
				String pool_id = list_pool_id.getValue();
				String sync_freq = list_syncfrequency.getValue();
				if (pool_id != null && sync_freq != null) {
					PrefManager.setWidgetPoolId(WidgetConfigurationActivity.this, appWidgetId, Integer.parseInt(pool_id));
					PrefManager.setWidgetSyncFrequency(WidgetConfigurationActivity.this, appWidgetId, Integer.parseInt(sync_freq));
					// change the result to OK
					Intent resultValue = new Intent();
					resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
					setResult(RESULT_OK, resultValue);
				}

				Intent clickIntent = new Intent(WidgetConfigurationActivity.this, MyWidgetProvider.class);

				clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
				clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});

				PendingIntent updatepending = PendingIntent.getBroadcast(WidgetConfigurationActivity.this, 0, clickIntent, 0); // PendingIntent.FLAG_UPDATE_CURRENT

				try {
					updatepending.send();
				} catch (CanceledException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
		setListFooter(btn);
	}

	@Override
	protected void onDestroy() {
		titles.recycle();
		syncFrequency.recycle();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
