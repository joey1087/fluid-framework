package com.sponberg.fluid.tool;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MakeViewsFile {

	static final String kResourcesDirName = "resources";

	static final String kViewsDir = "views";
	
	static final String kScreensDir = kViewsDir + "/screens";
	
	static final String kComponentsDir = kViewsDir + "/components";
	
	static final String kTableLayoutsDir = kViewsDir + "/table-layouts";
	
	static final String kGeneratedDirName = "generated";
	
	DecimalFormat versionNumberFormat = new DecimalFormat("###0.##");

	String workingDir;
	
	public MakeViewsFile(String workingDir) throws Exception {

		System.out.println("Running MakeViewsFile");
		
		this.workingDir = workingDir;
		
		TreeSet<String> screens = new TreeSet<>();
		
		TreeSet<String> components = new TreeSet<>();
		
		TreeSet<String> tableLayouts = new TreeSet<>();
		
		for (File file : getViewFiles(workingDir + "/" + kResourcesDirName + "/" + kScreensDir)) {
			screens.add(file.getName());
		}

		for (File file : getViewFiles(workingDir + "/" + kResourcesDirName + "/" + kComponentsDir)) {
			components.add(file.getName());
		}
		
		for (File file : getViewFiles(workingDir + "/" + kResourcesDirName + "/" + kTableLayoutsDir)) {
			tableLayouts.add(file.getName());
		}
		
		FileWriter writer = new FileWriter(workingDir + "/"
				+ kResourcesDirName + "/" + kGeneratedDirName
				+ "/views.txt");
		writer.write("screens:\n");
		for (String key : screens)
			writer.write("\t" + key + "\n");
		writer.write("\ncomponents:\n");
		for (String key : components)
			writer.write("\t" + key + "\n");
		writer.write("\ntable-layouts:\n");
		for (String key : tableLayouts)
			writer.write("\t" + key + "\n");
		writer.close();
		
		System.out.println("Finished Running MakeViewsFile");
	}

	static List<File> getViewFiles(String resourcesDir) {

		ArrayList<File> files = new ArrayList<>();

		File folder = new File(resourcesDir);

		if (!folder.exists()) {
			throw new RuntimeException("Folder doesn't exist " + resourcesDir);
		}
		
		getViewsFilesHelper(folder, files);

		return files;
	}

	static void getViewsFilesHelper(File folder, ArrayList<File> files) {
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				if (file.getName().startsWith("."))
					continue;
				files.add(file);
			} else if (file.isDirectory()) {
				getViewsFilesHelper(file, files);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {

		String workingDir = (args.length > 0) ? args[0] : ".";

		new MakeViewsFile(workingDir);
	}

}
