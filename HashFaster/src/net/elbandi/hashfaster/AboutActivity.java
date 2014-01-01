package net.elbandi.hashfaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import de.schildbach.wallet.litecoin.integration.android.LitecoinIntegration;
import net.elbandi.hashfaster.network.R;
import net.elbandi.hashfaster.utils.BitmapUtils;

public class AboutActivity extends SherlockActivity {
	private static final int REQUEST_CODE = 0;
	Button mDonate;
	TextView mAddress;
	ImageView img;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_about);

		mDonate = (Button) findViewById(R.id.btn_save_address);
		mAddress = (TextView) findViewById(R.id.tv_address);

		mDonate.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
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
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.copied_donation_link), Toast.LENGTH_LONG).show();

			}
		});
		OnClickListener donatedclick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				LitecoinIntegration.requestForResult(AboutActivity.this, REQUEST_CODE, mAddress.getText().toString());
			}
		};
		mAddress.setOnClickListener(donatedclick);
		img = (ImageView) findViewById(R.id.request_coins_qr);
		img.setImageBitmap(BitmapUtils.getQRCodeBitmap(mAddress.getText().toString(), 300));
		img.setOnClickListener(donatedclick);
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
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
/*
				final String txHash = LitecoinIntegration.transactionHashFromResult(data);
				if (txHash != null) {
					final SpannableStringBuilder messageBuilder = new SpannableStringBuilder("Transaction hash:\n");
					messageBuilder.append(txHash);
					messageBuilder.setSpan(new TypefaceSpan("monospace"), messageBuilder.length() - txHash.length(), messageBuilder.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					donateMessage.setText(messageBuilder);
					donateMessage.setVisibility(View.VISIBLE);
				}

				donateButton.setEnabled(false);
				donateButton.setText("Already donated");
*/
				Toast.makeText(this, "Thank you!", Toast.LENGTH_LONG).show();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "Cancelled.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Unknown result.", Toast.LENGTH_LONG).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);

		}
	}
}
