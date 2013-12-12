package net.elbandi.hashfaster.controls;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class MyEditTextPreference extends EditTextPreference {

	public MyEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyEditTextPreference(Context context) {
		super(context);
	}

	public void reloadInitialValue() {
		onSetInitialValue(true, null);
	}
}
