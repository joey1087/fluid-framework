package com.sponberg.fluid.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sponberg.fluid.util.StreamUtil;

public class ConvertIosImages {

	public static void main(String[] args) throws Exception {

		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"PNG Images", "png");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File parentDir = null;
		
		for (File file : chooser.getSelectedFiles()) {
			
			if (parentDir == null) {
				parentDir = new File(file.getParentFile().getAbsoluteFile() + "/converted");
				if (!parentDir.exists())
					parentDir.mkdir();
			}
			
			String name = file.getName();
			int i = name.lastIndexOf(".");
			
			String ext = name.substring(i + 1);
			
			String newName;
			
			String nameWithoutExt = name.substring(0, i);
			if (nameWithoutExt.endsWith("@4x")) {
				newName = nameWithoutExt.substring(0, i - 3) + "@c." + ext;
			} else if (nameWithoutExt.endsWith("@2x")) {
				newName = nameWithoutExt.substring(0, i - 3) + "@b." + ext;				
			} else {
				newName = nameWithoutExt.substring(0, i) + "@a." + ext;
			}
			
			newName = parentDir.getAbsolutePath() + "/" + newName;
			
			System.out.println(newName);
			
			StreamUtil.copyInputStream(new FileInputStream(file), new FileOutputStream(newName));
		}
		
	}

}
