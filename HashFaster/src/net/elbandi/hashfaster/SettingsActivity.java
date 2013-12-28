package net.elbandi.hashfaster;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.controls.MyEditTextPreference;
import net.elbandi.hashfaster.qr.IntentIntegrator;
import net.elbandi.hashfaster.qr.IntentResult;

public class SettingsActivity extends SherlockPreferenceActivity {

	MyEditTextPreference mAPIKey;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		PreferenceManager preferenceManager = getPreferenceManager();
		preferenceManager.setSharedPreferencesName(getApplicationContext().getPackageName());
		preferenceManager.setSharedPreferencesMode(MODE_PRIVATE);
		addPreferencesFromResource(R.xml.preferences);

		Intent intent = getIntent();
		String apikey = intent.getStringExtra(MainActivity.ARG_APIKEY);
		mAPIKey = (MyEditTextPreference) findPreference(getString(R.string.settings_api_key));
		mAPIKey.setKey(getString(R.string.settings_api_key) + "_" + apikey);
		mAPIKey.reloadInitialValue();
		Preference mQRScan = (Preference) findPreference(getString(R.string.settings_qr_scan));
		mQRScan.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference v) {
				IntentIntegrator integrator = new IntentIntegrator(SettingsActivity.this);
				integrator.initiateScan();
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			String result = scanResult.getContents();
			if (result.startsWith("|")) {
				try {
					result = result.split("\\|")[2];
				} catch (Exception e) {
				}
			}
			mAPIKey.setText(result);
		}
	}
}
