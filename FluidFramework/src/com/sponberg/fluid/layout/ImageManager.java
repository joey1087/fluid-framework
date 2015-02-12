package com.sponberg.fluid.layout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.Logger;

public class ImageManager implements ApplicationInitializer {

	HashMap<String, ArrayList<Image>> imagesByName = new HashMap<>();

	@Override
	public void initialize(FluidApp app) {
		readImagesFile(app);
	}

	public void readImagesFile(FluidApp app) {
		try {
			BufferedReader in = new BufferedReader(
					new StringReader(app.getResourceService()
							.getResourceAsString("generated", "images.txt")));
			String line;
			while ((line = in.readLine()) != null) {
				String[] sa = line.split(":");
				ArrayList<Image> images = new ArrayList<>();
				imagesByName.put(sa[0], images);
				for (int index = 1; index < sa.length; index += 2) {
					String key = sa[index];
					String[] d = sa[index + 1].split(",");
					images.add(new Image(key, Integer.parseInt(d[0]), Integer.parseInt(d[1])));
				}
			}
		} catch (IOException e) {
			Logger.error(this, e);
		}
	}

	public String getImageName(String key, int w, int h) {
		
		w = (int) (1.0 / GlobalState.fluidApp.getViewManager().getDevicePixelActualMultiplier() * w);
		h = (int) (1.0 / GlobalState.fluidApp.getViewManager().getDevicePixelActualMultiplier() * h);
		
		ArrayList<Image> images = imagesByName.get(key);
		if (images == null) {
			return null;
		}
		for (int index = 0; index < images.size(); index++) {
			Image i = images.get(index);
			if (i.w >= w) {
				return key + "@" + i.key + ".png";
			}
		}
		return key + "@" + images.get(images.size() - 1).key + ".png";
	}
	
	public double getImageAspectRatio(String key) {
		ArrayList<Image> images = imagesByName.get(key);
		if (images == null) {
			return 1d;
		}
		Image i = images.get(0);
		return i.w * 1.0 / i.h;
	}
	
	public ImageBounds getImageBounds(String key, int w, int h) {
		
		ArrayList<Image> images = imagesByName.get(key);
		if (images == null) {
			return null;
		}
		for (int index = 0; index < images.size(); index++) {
			Image i = images.get(index);
			if (i.w > w) {
				return new ImageBounds(i);
			}
		}
		return new ImageBounds(images.get(images.size() - 1));
	}
	
	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
}

@Data
class ImageBounds {
	
	final int w, h;

	public ImageBounds(Image image) {
		this.w = image.w;
		this.h = image.h;
	}
	
}

class Image {

	final String key;
	
	final int w, h;
		
	public Image(String key, int w, int h) {
		this.key = key;
		this.w = w;
		this.h = h;
	}
	
}