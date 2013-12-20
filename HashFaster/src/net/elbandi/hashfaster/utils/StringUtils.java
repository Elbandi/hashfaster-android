package net.elbandi.hashfaster.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedHashMap;

public class StringUtils {
	static final String TAG = StringUtils.class.getName();
	static final int BUFFER_SIZE = 10240;

	public static String readAll(InputStream in, String encoding) throws IOException {
		Writer out = new StringWriter();
		char[] buf = new char[BUFFER_SIZE];
		try {
			Reader reader;
			if (encoding != null) {
				reader = new InputStreamReader(in, encoding);
			} else {
				reader = new InputStreamReader(in);
			}
			int len;
			while ((len = reader.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		} finally {
			in.close();
		}
		return out.toString();
	}

	public static String readAll(InputStream is) {
		try {
			return readAll(is, null);
		} catch (UnsupportedEncodingException e) {
			// Since this is going to use the default encoding, it is never
			// going to crash on an UnsupportedEncodingException
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static LinkedHashMap<String, String> LoadStringArray(Context context, int keyResourceId, int valueResourceId) {
		LinkedHashMap<String, String> array = new LinkedHashMap<String, String>();
		TypedArray keys = context.getResources().obtainTypedArray(keyResourceId);
		TypedArray values = context.getResources().obtainTypedArray(valueResourceId);
		try {
			int nkeys = keys.length();
			int nvalues = keys.length();
			for (int i = 0; i < nkeys; i++) {
				if (i < nvalues)
					array.put(keys.getString(i), values.getString(i));
				else
					array.put(keys.getString(i), null);
			}
		} finally {
			keys.recycle();
			values.recycle();
		}
		return array;
	}
	public static LinkedHashMap<String, Drawable> LoadDrawableArray(Context context, int keyResourceId, int valueResourceId) {
		LinkedHashMap<String, Drawable> array = new LinkedHashMap<String, Drawable>();
		TypedArray keys = context.getResources().obtainTypedArray(keyResourceId);
		TypedArray values = context.getResources().obtainTypedArray(valueResourceId);
		try {
			int nkeys = keys.length();
			int nvalues = keys.length();
			for (int i = 0; i < nkeys; i++) {
				if (i < nvalues)
					array.put(keys.getString(i), values.getDrawable(i));
				else
					array.put(keys.getString(i), null);
			}
		} finally {
			keys.recycle();
			values.recycle();
		}
		return array;
	}
}
