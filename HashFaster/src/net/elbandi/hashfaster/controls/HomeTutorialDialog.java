package net.elbandi.hashfaster.controls;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.managers.PrefManager;

public class HomeTutorialDialog extends Dialog {

	Context mContext;

	public HomeTutorialDialog(Context context) {
		super(context);
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
		this.setContentView(R.layout.tutorial_home);
		
		RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
		
		container.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PrefManager.setSeenHomeTutorial(mContext, true);
				dismiss();
			}
		});
	}
}
