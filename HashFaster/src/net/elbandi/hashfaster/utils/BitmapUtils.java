package net.elbandi.hashfaster.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Display;

import java.util.Hashtable;

public class BitmapUtils {
	public final static QRCodeWriter QR_CODE_WRITER = new QRCodeWriter();
	public static Bitmap getQRCodeBitmap(final String url, final int size) {
		try {
			final Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.MARGIN, 2);

			final BitMatrix result = QR_CODE_WRITER.encode(url, BarcodeFormat.QR_CODE, size, size, hints);

			final int width = result.getWidth();
			final int height = result.getHeight();
			final int[] pixels = new int[width * height];

			for (int y = 0; y < height; y++) {
				final int offset = y * width;
				for (int x = 0; x < width; x++) {
					pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.TRANSPARENT;
				}
			}

			final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (final WriterException x) {
			x.printStackTrace();
			return null;
		}
	}
	public static int getWidthByContext(Context context) {
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics.widthPixels;
	}
}
