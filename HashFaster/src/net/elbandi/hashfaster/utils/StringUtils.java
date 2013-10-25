package net.elbandi.hashfaster.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

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
}
