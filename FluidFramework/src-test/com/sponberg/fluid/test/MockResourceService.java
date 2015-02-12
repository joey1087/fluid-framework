package com.sponberg.fluid.test;

import java.io.IOException;

import com.sponberg.fluid.ResourceService;
import com.sponberg.fluid.util.FileResourceService;

public class MockResourceService implements ResourceService {

	FileResourceService service = new FileResourceService();
	
	public MockResourceService() {
	}
	
	public MockResourceService(String workingDir) {
		service.setWorkingDir(workingDir);
	}
	
	@Override
	public String getResourceAsString(String dir, String name) {
		return service.getResourceAsString(dir, name);
	}

	@Override
	public byte[] getResourceAsBytes(String dir, String name) {
		return getResourceAsString(dir, name).getBytes();
	}

	@Override
	public void saveResource(String dir, String name, byte[] bytes, boolean excludeFromBackup)
			throws IOException {
		service.saveResource(dir, name, bytes, excludeFromBackup);
	}

	@Override
	public boolean resourceExists(String dir, String name) {
		return service.resourceExists(dir, name);
	}

}
