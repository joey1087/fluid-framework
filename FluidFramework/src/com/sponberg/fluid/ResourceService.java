package com.sponberg.fluid;

import java.io.IOException;

public interface ResourceService {

	public String getResourceAsString(String dir, String name);
	
	public byte[] getResourceAsBytes(String dir, String name);
	
	public void saveResource(String dir, String name, byte[] bytes, boolean excludeFromBackup) throws IOException;
	
	public boolean resourceExists(String dir, String name);
	
}
