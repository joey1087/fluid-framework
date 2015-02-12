package com.sponberg.fluid.tool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;

public class MakeImagesFile {

	static final String kImagesFileName = "images.txt";

	static final String kResourcesDirName = "resources";

	static final String kImagesDir = "images";

	static final String kGeneratedDirName = "generated";

	HashMap<String, TreeSet<ImageInfo>> imagesByName = new HashMap<>();

	public MakeImagesFile(String workingDir) throws IOException {
		
		System.out.println("Runing MakeImagesFile");
		
		for (File file : getImageFiles(workingDir + "/" + kResourcesDirName + "/" + kImagesDir)) {

			if (file.getName().startsWith("."))
				continue;
			
			String name = file.getName();
			int i = name.indexOf("@");
			if (i == -1) {
				throw new RuntimeException("Image doesn't contain '@' " + name);
			}
			int i2 = name.indexOf(".", i + 1);

			String base = name.substring(0, i);
			String sizeToken = name.substring(i + 1, i2);
			
			ImageInfo ii = new ImageInfo();
			ii.sizeToken = sizeToken;
			setImageSize(file, ii);

			TreeSet<ImageInfo> set = imagesByName.get(base);
			if (set == null) {
				set = new TreeSet<ImageInfo>();
				imagesByName.put(base, set);
			}
			set.add(ii);
		}
	
		FileWriter out = new FileWriter(workingDir + "/" + kResourcesDirName + "/" + kGeneratedDirName + "/" + kImagesFileName);
		
		TreeSet<String> keysSorted = new TreeSet(imagesByName.keySet());
		for (String key : keysSorted) {
			out.append(key);
			for (ImageInfo ii : imagesByName.get(key)) {
				out.append(":" + ii.sizeToken + ":");
				out.append(ii.width + ",");
				out.append(ii.height + "");
			}
			out.append("\n");
		}
		
		out.close();
		
		System.out.println("Done");
	}

	void setImageSize(File file, ImageInfo imageInfo) throws IOException {
		long time = System.currentTimeMillis();
		BufferedImage bimg = ImageIO.read(file);
		imageInfo.width = bimg.getWidth();
		imageInfo.height = bimg.getHeight();
		System.out.println((System.currentTimeMillis() - time));
	}
	
	public static void main(String[] args) throws IOException {

		String workingDir = (args.length > 0) ? args[0] : ".";

		new MakeImagesFile(workingDir);
	}

	static List<File> getImageFiles(String resourcesDir) {

		ArrayList<File> files = new ArrayList<>();

		File folder = new File(resourcesDir);

		if (!folder.exists()) {
			throw new RuntimeException("Folder doesn't exist " + resourcesDir);
		}
		
		getImageFilesHelper(folder, files);

		return files;
	}

	static void getImageFilesHelper(File folder, ArrayList<File> files) {
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				getImageFilesHelper(file, files);
			}
		}
	}

	static class ImageInfo implements Comparable<ImageInfo> {

		String sizeToken;

		int width;

		int height;

		@Override
		public int compareTo(ImageInfo o) {
			return sizeToken.compareTo(o.sizeToken);
		}

		@Override
		public int hashCode() {
			return sizeToken.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return sizeToken.equals(((ImageInfo) obj).sizeToken);
		}
		
	}

}
