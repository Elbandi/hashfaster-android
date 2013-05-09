package com.inajstudios.wemineltc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutActivity extends SherlockActivity {
	Button mSave, mRaw, mRefresh, mDonate;
	TextView mUsername, mRewards, mRoundEstimate, mHashrate, mPayoutHistory, mRoundShares, mTimestamp, mAddress;
	EditText mAPIKey;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_about);

		mDonate = (Button) findViewById(R.id.btn_save_address);
		mAddress = (TextView) findViewById(R.id.tv_address);

		mDonate.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String stringYouExtracted = mAddress.getText().toString();
				int startIndex = 0;
				int endIndex = stringYouExtracted.length();
				stringYouExtracted = stringYouExtracted.substring(startIndex, endIndex);
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
					android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					clipboard.setText(stringYouExtracted);
				} else {
					android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", stringYouExtracted);
					clipboard.setPrimaryClip(clip);
				}
				Toast.makeText(getApplicationContext(), "Copied Donation Link!", Toast.LENGTH_LONG).show();

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
