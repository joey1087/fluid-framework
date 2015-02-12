package com.sponberg.fluid.tool;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

public class MakeDatastoreVersions {

	static final String kResourcesDirName = "resources";

	static final String kSqlDirName = "sql";
	
	static final String kGeneratedDirName = "generated";
	
	DecimalFormat versionNumberFormat = new DecimalFormat("###0.##");

	String workingDir;
	
	public MakeDatastoreVersions(String workingDir) throws Exception {

		System.out.println("Running MakeDatastoreVersions");
		
		this.workingDir = workingDir;
		
		HashMap<String, TreeSet<Double>> databaseVersions = new HashMap<>();
		
		for (File file : getSqlFiles(workingDir + "/" + kResourcesDirName + "/" + kSqlDirName)) {
			String name = file.getName();
			String[] tokens = name.split("[_\\.]");
			if (name.startsWith(".")) // Apple places .DS_Store
				continue;
			String databaseName = tokens[0];
			String version = tokens[2];
			String subversion = tokens[3];
			double v = versionNumberFormat.parse(version + "." + subversion).doubleValue();
			
			TreeSet<Double> versions = databaseVersions.get(databaseName);
			if (versions == null) {
				versions = new TreeSet<Double>();
				databaseVersions.put(databaseName, versions);
			}
			
			versions.add(v);
		}

		for (Entry<String, TreeSet<Double>> entry : databaseVersions.entrySet()) {
			FileWriter writer = new FileWriter(workingDir + "/"
					+ kResourcesDirName + "/" + kGeneratedDirName
					+ "/datastoreVersions_" + entry.getKey() + ".txt");
			for (Double d : entry.getValue()) {
				writer.write(versionNumberFormat.format(d) + "\n");
			}
			writer.close();
		}
		
		FileWriter writer = new FileWriter(workingDir + "/"
				+ kResourcesDirName + "/" + kGeneratedDirName
				+ "/datastores.txt");
		for (String key : databaseVersions.keySet())
			writer.write(key + "\n");
		writer.close();
		
		System.out.println("Finished Running MakeDatastoreVersions");
	}

	static List<File> getSqlFiles(String resourcesDir) {

		ArrayList<File> files = new ArrayList<>();

		File folder = new File(resourcesDir);

		if (!folder.exists()) {
			throw new RuntimeException("Folder doesn't exist " + resourcesDir);
		}
		
		getSqlFilesHelper(folder, files);

		return files;
	}

	static void getSqlFilesHelper(File folder, ArrayList<File> files) {
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				if (file.getName().startsWith("."))
					continue;
				files.add(file);
			} else if (file.isDirectory()) {
				getSqlFilesHelper(file, files);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {

		String workingDir = (args.length > 0) ? args[0] : ".";

		new MakeDatastoreVersions(workingDir);
	}

}
