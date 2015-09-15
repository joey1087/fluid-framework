package com.sponberg.fluid.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.sponberg.fluid.ResourceService;
import com.sponberg.fluid.util.StreamUtil;

public class DefaultResourceService implements ResourceService {

	private final Context context;

	private String kRoot = "fluid";
	
	public DefaultResourceService(Context context) {
		this.context = context;
	}
	
	@Override
	public String getResourceAsString(String dir, String name) {
		
		byte[] bytes = getResourceAsBytes(dir, name);
		if (bytes != null)
			try {
				return new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return new String(bytes);
			}
		else
			return null;
	}
	
	protected File getFile(File dir, String name) {
		return new File(dir.getAbsolutePath() + "/" + name);
	}
	
	@Override
	public byte[] getResourceAsBytes(String dir, String name) {
		
		File file;
		
		if (!dir.equals("")) {
			dir = dir + "/";
			File dirFile = context.getDir(kRoot, 0);
			dirFile = new File(dirFile, dir);
			file = getFile(dirFile, name);
		} else {
			file = getFile(context.getFilesDir(), name);
		}
				
		try {
			InputStream in;
			if (file.exists()) {
				in = new FileInputStream(file);
			} else {
				AssetManager am = context.getAssets();
				in = am.open(kRoot + "/" + dir + name);			
			}
		
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			StreamUtil.copyInputStream(in, out);
			return out.toByteArray();	
		} catch (IOException e) {
			return null;
		}		
	}

	@Override
	public boolean resourceExists(String dir, String name) {
		
		File file;
		
		if (!dir.equals("")) {
			dir = dir + "/";
			File dirFile = context.getDir(kRoot, 0);
			dirFile = new File(dirFile, dir);
			file = getFile(dirFile, name);
		} else {
			file = getFile(context.getFilesDir(), name);
		}

		if (file.exists()) {
			return true;
		}
		
		AssetManager am = context.getAssets();

		try {
			am.open(kRoot + "/" + dir + name);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void saveResource(String dir, String name, byte[] bytes, boolean excludeFromBackupIgnored)
			throws IOException {

		File file;
		
		if (!dir.equals("")) {
			dir = dir + "/";
			File dirFile = context.getDir(kRoot, 0);
			dirFile = new File(dirFile, dir);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			file = getFile(dirFile, name);
		} else {
			file = getFile(context.getFilesDir(), name);
		}
		
		StreamUtil.copyInputStream(new ByteArrayInputStream(bytes), new FileOutputStream(file));
	}

	@Override
	public Object getImage(String dir, String name) {
		
		File imageFile;

		if (dir != null && !dir.equals("")) {
			dir = dir + "/";
			File dirFile = context.getDir(kRoot, 0);
			dirFile = new File(dirFile, dir);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			imageFile = getFile(dirFile, name);
		} else {
			imageFile = getFile(context.getFilesDir(), name);
		}
		
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(imageFile.toString());
			// COMPRESS HERE
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void saveImage(String dir, String name, Object object, boolean excludeFromBackup) throws IOException {
		
		if (object instanceof Bitmap) {
			File imageFile;
			
			if (!dir.equals("")) {
				dir = dir + "/";
				File dirFile = context.getDir(kRoot, 0);
				dirFile = new File(dirFile, dir);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
				imageFile = getFile(dirFile, name);
			} else {
				imageFile = getFile(context.getFilesDir(), name);
			}
			Bitmap bitmap = (Bitmap) object;
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
		    bitmap.compress(CompressFormat.JPEG, 100, buffer);
		    byte[] byteArray = buffer.toByteArray();
		    
			StreamUtil.copyInputStream(new ByteArrayInputStream(byteArray), new FileOutputStream(imageFile));
		}
	}
}
