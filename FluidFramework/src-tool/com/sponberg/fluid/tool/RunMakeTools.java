package com.sponberg.fluid.tool;

public class RunMakeTools {

	public static void main(String[] args) throws Exception {

		String workingDir = (args.length > 0) ? args[0] : ".";

		new MakeDatastoreClasses(workingDir);
	}

}
