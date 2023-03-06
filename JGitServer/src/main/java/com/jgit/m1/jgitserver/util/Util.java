package com.jgit.m1.jgitserver.util;

import java.io.File;

/**
 * @author fayss
 *
 */
public class Util {

	public static void deleteFolder(String folderPath) {
		File folder = new File(folderPath);

		if (folder.exists()) {
			helperDeleteFolder(folder);
		} else {
			System.out.println(".jgit Folder doesn't exist");
		}
	}

	public static void helperDeleteFolder(File folder) {
		File[] files = folder.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					helperDeleteFolder(file);
				} else {
					file.delete();
				}
			}
		}

		folder.delete();
	}
}
