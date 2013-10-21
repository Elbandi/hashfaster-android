package net.elbandi.hashfaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.managers.PrefManager;
import net.elbandi.hashfaster.qr.IntentIntegrator;
import net.elbandi.hashfaster.qr.IntentResult;

public class SettingsActivity extends SherlockActivity {

	Button mSave, mQRScan;
	EditText mAPIKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);

		mQRScan = (Button) findViewById(R.id.btn_qr_scan);
		mSave = (Button) findViewById(R.id.btn_save);
		mAPIKey = (EditText) findViewById(R.id.et_api);

		mAPIKey.setText(PrefManager.getAPIKey(getApplicationContext()));

		mSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String APIKey = mAPIKey.getText().toString();
				PrefManager.setAPIKey(getApplicationContext(), mAPIKey.getText().toString());
				Toast.makeText(getApplicationContext(), "Saved your API key", Toast.LENGTH_LONG).show();
			}
		});

		mQRScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentIntegrator integrator = new IntentIntegrator(SettingsActivity.this);
				integrator.initiateScan();
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
			mAPIKey.setText(scanResult.getContents());
		}
	}
}
