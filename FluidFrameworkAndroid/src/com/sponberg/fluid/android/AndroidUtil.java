package com.sponberg.fluid.android;

import com.sponberg.fluid.GlobalState;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AndroidUtil {

	public static Bitmap getBitmapFromAssets(String dir, String name) {

		byte[] bytes = GlobalState.fluidApp.getResourceService()
				.getResourceAsBytes(dir, name);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
				options);
		// Bitmap bitmap = BitmapFactory.decodeStream(new
		// ByteArrayInputStream(bytes), );

		//testWriteToFile(bitmap);

		return bitmap;
	}

	/*
	static void testWriteToFile(Bitmap bmp) {
		
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
                "/hstdbc.png";
		
		System.out.println("hstdbc " + file_path);
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file_path);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Throwable ignore) {
			}
		}
	}*/

}
