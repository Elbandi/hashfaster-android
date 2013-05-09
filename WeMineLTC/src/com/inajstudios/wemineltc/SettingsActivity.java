package com.inajstudios.wemineltc;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.inajstudios.wemineltc.managers.PrefManager;

public class SettingsActivity extends SherlockActivity {

	Button mSave;
	EditText mAPIKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);

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
