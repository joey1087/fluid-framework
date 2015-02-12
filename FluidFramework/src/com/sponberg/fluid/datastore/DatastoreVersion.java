package com.sponberg.fluid.datastore;

import lombok.Data;

@Data
public class DatastoreVersion implements Comparable<DatastoreVersion> {

	final int majorVersion;
	
	final int minorVersion;

	public DatastoreVersion(final int majorVersion, final int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	@Override
	public int compareTo(DatastoreVersion o) {
		int i = majorVersion - o.majorVersion;
		if (i != 0) {
			return i;
		} else {
			return minorVersion - o.minorVersion;
		}
	}
	
}
