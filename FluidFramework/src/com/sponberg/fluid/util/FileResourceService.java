package com.sponberg.fluid.util;

import java.io.File;
import java.io.IOException;

import com.sponberg.fluid.ResourceService;

public class FileResourceService implements ResourceService {

	private static final String kRoot = "resources";
	
	String workingDir = null;
	
	@Override
	public String getResourceAsString(String dir, String name) {
		if (!dir.equals("")) {
			dir = dir + "/";
		}
		
		File file;
		if (workingDir == null) {
			file = new File(kRoot + "/" + dir + name);
		} else {
			file = new File(workingDir + "/" + kRoot + "/" + dir + name);
		}
		try {
			return StreamUtil.fileToString(file.getAbsolutePath());
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public byte[] getResourceAsBytes(String dir, String name) {
		return getResourceAsString(dir, name).getBytes();
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	@Override
	public void saveResource(String dir, String name, byte[] bytes, boolean excludeFromBackup)
			throws IOException {

		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean resourceExists(String dir, String name) {

		throw new RuntimeException("Not implemented");
	}
	
}
