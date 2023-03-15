package fr.uca.jgit.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

import com.sun.jdi.request.DuplicateRequestException;

import fr.uca.jgit.model.WorkingDirectory;

public class Add extends Command {

	private static final Logger logger = Logger.getLogger(StateCommit.class.getName());

	// the goal is to add relative paths of files into an index,
	// under the condition that said paths aren't already present into the index
	// --> if the parameter is a file, we browse through the whole working directory
	// in order to find it
	// --> if the parameter is a folder then we add all the files and directories
	// within the said folder

	@Override
	public void execute(String... args) {
		if (args.length == 0) {
			System.out.println("No path specified.");
			return;
		}
		String path = args[0];
		// path is a directory
		if (Files.isDirectory(WorkingDirectory.getInstance().getPath(path))) {
			File[] files = new File(WorkingDirectory.getInstance().getPath(path).toString()).listFiles();
			if (files != null) {
				for (File file : files) {
					if (path.equals("."))
						path = "";
					if (file.isDirectory()) {
						execute(Paths.get(path, file.getName()).toString());
					} else {
						fileExists(WorkingDirectory.getInstance().getPath(path, file.getName()).toString());
					}
				}
			}
			return;
		} else if (Files.isRegularFile(WorkingDirectory.getInstance().getPath(path))) {
			fileExists(WorkingDirectory.getInstance().getPath(path).toString());
			return;
		}
	}

	private void fileExists(String path) {
		File index = super.wd.getPath(".jgit", "index").toFile();
		if (!index.exists()) {
			try {
				Files.createFile(super.wd.getPath(".jgit", "index"));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		File currentFile = Paths.get(path).toFile();
		try {
			try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (currentFile.toPath().toString().equals(line)) {
						throw new DuplicateRequestException("The file " + path + " is already in the index.");
					}
				}
				reader.close();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			return;
		}
		addFileToIndex(currentFile);
	}

	private void addFileToIndex(File file) {
		try {
			Files.write(super.wd.getPath(".jgit", "index"), (file.toPath().toString() + "\n").getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			logger.info(e.getMessage());
			return;
		}
	}
}