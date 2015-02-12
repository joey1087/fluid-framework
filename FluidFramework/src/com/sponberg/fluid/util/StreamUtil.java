package com.sponberg.fluid.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	public static final String fileToString(String file) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamUtil.copyInputStream(new FileInputStream(file), out);
		return new String(out.toByteArray());
	}
	
	public static final void copyInputStream(InputStream in, OutputStream out)
		throws IOException {
		byte[] buffer = new byte[1024];
		int len;
	
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
	
		in.close();
		out.close();
	}

}
