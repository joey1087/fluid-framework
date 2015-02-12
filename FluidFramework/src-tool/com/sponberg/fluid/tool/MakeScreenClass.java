package com.sponberg.fluid.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.sponberg.fluid.parser.SettingsParser;
import com.sponberg.fluid.test.MockApp;
import com.sponberg.fluid.test.MockResourceService;
import com.sponberg.fluid.util.KVLReader;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.StringUtil;

public class MakeScreenClass {

	static final String kResourcesDirName = "resources";

	static final String kViewsDir = "views";
	
	static final String kScreensDir = kViewsDir + "/screens";
	
	static final String kComponentsDir = kViewsDir + "/components";
	
	static final String kTableLayoutsDir = kViewsDir + "/table-layouts";
	
	String workingDir;
	
	String packageName;
	
	MockApp mockApp = new MockApp();
	
	HashMap<String, HashSet<String>> screenViews = new HashMap<>();
	
	public MakeScreenClass(String workingDir) throws Exception {

		System.out.println("Running MakeScreenClass");
		
		this.workingDir = workingDir;
		
		mockApp.setResourceService(new MockResourceService(workingDir));
		
		SettingsParser parser = new SettingsParser();
		parser.initialize(mockApp);
		
		this.packageName = mockApp.getSettings().getValue("base-package") + ".ui";
		
		StringBuilder screenNames = new StringBuilder();
		StringBuilder tableLayoutNames = new StringBuilder();
		StringBuilder componentNames = new StringBuilder();
		StringBuilder screens = new StringBuilder();
		StringBuilder tableLayouts = new StringBuilder();
		StringBuilder components = new StringBuilder();
		
		for (File file : getViewFiles(workingDir + "/" + kResourcesDirName + "/" + kScreensDir)) {
			if (file.getName().contains("@"))
				continue;
			writeScreen(file, screenNames, screens);
		}

		for (File file : getViewFiles(workingDir + "/" + kResourcesDirName + "/" + kTableLayoutsDir)) {
			if (file.getName().contains("@"))
				continue;
			writeTableLayout(file, tableLayoutNames, tableLayouts);
		}
		
		for (File file : getViewFiles(workingDir + "/" + kResourcesDirName + "/" + kComponentsDir)) {
			if (file.getName().contains("@"))
				continue;
			writeComponent(file, componentNames, components);
		}
		
		StringBuilder builder = new StringBuilder();
		writeTop(builder);
		builder.append(screenNames.toString());
		builder.append(tableLayoutNames.toString());
		builder.append(componentNames.toString());
		builder.append(screens.toString());
		builder.append(tableLayouts.toString());
		builder.append(components.toString());
		writeBottom(builder);
		
		File dir = new File(workingDir + "/src/" + packageName.replaceAll("\\.", "/"));
		dir.mkdirs();
		FileWriter writer = new FileWriter(dir.getAbsolutePath() + "/" + "Screen.java");
		writer.write(builder.toString());
		writer.close();
		
		System.out.println("Finished Running MakeScreenClass");
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
	
	void writeScreen(File file, StringBuilder screenNames, StringBuilder screens) throws FileNotFoundException, IOException {
		
		String suffix = "";
		if (file.getName().contains("@")) {
			suffix = file.getName().substring(file.getName().indexOf("@") + 1);
			suffix = suffix.substring(0, suffix.indexOf("."));
			suffix = StringUtil.capitalized(suffix);
		}
		
		KeyValueList kvl = new KVLReader(new BufferedReader(new FileReader(file)));

		int i = file.getName().indexOf(".");
		String screenId = file.getName().substring(0, i);
		
		if (suffix.equals("")) {
			screenNames.append("\tpublic static final String ");
			screenNames.append(StringUtil.underscoreDashToCamelCase(screenId));
			screenNames.append(" = \"");
			screenNames.append(screenId);
			screenNames.append("\";\n\n");
		}

		screens.append("\tpublic static final class Screen" + StringUtil.underscoreDashToCamelCase(screenId) + " {\n\n");
		
		for (String string : kvl.getValues("views")) {
			screens.append("\t\tpublic static final String ");
			screens.append(StringUtil.underscoreDashToCamelCase(string));
			screens.append(" = \"");
			screens.append(string);
			screens.append("\";\n\n");
		}
		
		screens.append("\t}\n\n");	
	}
	
	void writeComponent(File file, StringBuilder compNames, StringBuilder builder) throws FileNotFoundException, IOException {
		
		String suffix = "";
		if (file.getName().contains("@")) {
			suffix = file.getName().substring(file.getName().indexOf("@") + 1);
			suffix = suffix.substring(0, suffix.indexOf("."));
			suffix = StringUtil.capitalized(suffix);
		}
		
		KeyValueList kvl = new KVLReader(new BufferedReader(new FileReader(file)));

		int i = file.getName().indexOf(".");
		String compId = file.getName().substring(0, i);
		
		String compValue = StringUtil.underscoreDashToCamelCase(compId);
		if (!compId.startsWith("Comp")) {
			compId = "Comp" + compValue;
		}
		
		if (suffix.equals("")) {
			compNames.append("\tpublic static final String ");
			compNames.append(StringUtil.underscoreDashToCamelCase(compValue));
			compNames.append(" = \"");
			compNames.append(compValue);
			compNames.append("\";\n\n");
		}
		
		builder.append("\tpublic static final class " + compId + " {\n\n");
		
		for (String string : kvl.getValues("views")) {
			builder.append("\t\tpublic static final String ");
			builder.append(StringUtil.underscoreDashToCamelCase(string));
			builder.append(" = \"");
			builder.append(string);
			builder.append("\";\n\n");
		}
		
		builder.append("\t}\n\n");	
	}
	
	void writeTableLayout(File file, StringBuilder compNames, StringBuilder builder) throws FileNotFoundException, IOException {
		
		String suffix = "";
		if (file.getName().contains("@")) {
			suffix = file.getName().substring(file.getName().indexOf("@") + 1);
			suffix = suffix.substring(0, suffix.indexOf("."));
			suffix = StringUtil.capitalized(suffix);
		}
		
		KeyValueList kvl = new KVLReader(new BufferedReader(new FileReader(file)));

		int i = file.getName().indexOf(".");
		String tableLayoutId = file.getName().substring(0, i);
		
		String tableLayoutName = tableLayoutId + suffix;

		String tableLayoutValue = StringUtil.underscoreDashToCamelCase(tableLayoutName);
		if (!tableLayoutName.startsWith("TL")) {
			tableLayoutName = "TL" + tableLayoutValue;
		}
		
		String className = "TableLayout" + tableLayoutName.substring(2);
		
		if (suffix.equals("")) {
			compNames.append("\tpublic static final String ");
			compNames.append(StringUtil.underscoreDashToCamelCase(tableLayoutValue));
			compNames.append(" = \"");
			compNames.append(tableLayoutValue);
			compNames.append("\";\n\n");
		}
		
		builder.append("\tpublic static final class " + className + " {\n\n");
		
		for (KeyValueList row : kvl.get("section-headers")) {
			
			String headerName = row.getValue();

			builder.append("\t\tpublic static final String ");
			builder.append(StringUtil.underscoreDashToCamelCase("SH_" + headerName)); // prefix with header_ to avoid conflict with rows
			builder.append(" = \"");
			builder.append(headerName);
			builder.append("\";\n\n");
		}		
		
		for (KeyValueList row : kvl.get("rows")) {
			
			String rowName = row.getValue();

			builder.append("\t\tpublic static final String ");
			builder.append(StringUtil.underscoreDashToCamelCase(rowName));
			builder.append(" = \"");
			builder.append(rowName);
			builder.append("\";\n\n");
		}
		
		for (KeyValueList row : kvl.get("section-headers")) {
			
			String headerName = row.getValue();
			
			builder.append("\t\tpublic static final class SectionHeader" + StringUtil.underscoreDashToCamelCase(headerName) + " {\n\n");
			
			for (String view : row.getValues("views")) {
				
				builder.append("\t\t\tpublic static final String ");
				builder.append(StringUtil.underscoreDashToCamelCase(view));
				builder.append(" = \"");
				builder.append(view);
				builder.append("\";\n\n");
			}
			
			builder.append("\t\t}\n\n");
		}
		
		for (KeyValueList row : kvl.get("rows")) {
			
			String rowName = row.getValue();
			
			builder.append("\t\tpublic static final class Row" + StringUtil.underscoreDashToCamelCase(rowName) + " {\n\n");
			
			for (String view : row.getValues("views")) {
				
				builder.append("\t\t\tpublic static final String ");
				builder.append(StringUtil.underscoreDashToCamelCase(view));
				builder.append(" = \"");
				builder.append(view);
				builder.append("\";\n\n");
			}
			
			builder.append("\t\t}\n\n");
		}
		
		builder.append("\t}\n\n");	
		
	}
	
	void writeTop(StringBuilder builder) {
		builder.append("// This class is AutoGenerated code by the MakeScreensClass tool.\n");
		builder.append("// DO NOT EDIT.\n\n");
		
		builder.append("package ");
		builder.append(packageName);
		builder.append(";\n\n");

		builder.append("public class Screen {\n\n");
	}
	
	void writeBottom(StringBuilder builder) {
		builder.append("}");
	}
	
	public static void main(String[] args) throws Exception {

		String workingDir = (args.length > 0) ? args[0] : ".";

		new MakeScreenClass(workingDir);
	}
	
}
