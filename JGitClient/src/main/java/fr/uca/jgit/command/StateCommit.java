package fr.uca.jgit.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import exception.WrongFileTypeException;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.TextFile;
import fr.uca.jgit.model.WorkingDirectory;

public class StateCommit extends Command {

	public WorkingDirectory wd = WorkingDirectory.getInstance();

	/**
	 * This method will start exploring from a given folder and add all the children
	 * to its children's list if one of the children is a folder it will call itself
	 * recursively. At the end the of the function call the jGitStartingFolder
	 * should be populated with all of its corresponding children in the form of a
	 * tree
	 * 
	 * @param startingFolder
	 * @param path
	 */
	public void populateWithChildren(String startingFolderPath, Folder jGitStartingFolder) {
		// get the list of children from the given path
		File systemStartingFolder = new File(startingFolderPath);
		if (systemStartingFolder.isDirectory()) {
			File[] children = systemStartingFolder.listFiles();
			for (File child : children) {
				String systemFileName = child.getName();
				// if the child is a text file
				if (child.isFile() && child.getName().endsWith(".txt")) {
					TextFile jGitTextFile = new TextFile();
					StringBuilder contentBuilder = new StringBuilder();
					try (BufferedReader reader = new BufferedReader(new FileReader(child))) {
						String line;
						while ((line = reader.readLine()) != null) {
							contentBuilder.append(line);
							contentBuilder.append(System.lineSeparator());
						}
					} catch (IOException e) {
						System.err.println("Error reading file: " + e.getMessage());
					}
					String content = contentBuilder.toString();
					jGitTextFile.setContent(content);
					jGitStartingFolder.add(systemFileName, jGitTextFile);
				}

				// if the child is a directory
				if (child.isDirectory()) {
					Folder jGitExploredFolder = new Folder();
					String exploredFolderPath = child.getPath();
					populateWithChildren(exploredFolderPath, jGitExploredFolder);
					jGitStartingFolder.add(systemFileName, jGitExploredFolder);
				}
			}
		} else {
			System.out.println(startingFolderPath + " is not a directory.");
		}
	}

	public void commitFromIndex() throws WrongFileTypeException {
		// read the index file into a list of files and folders
		List<String> indexLines = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./.jgit/HEAD")); // Create a new BufferedReader
																						// to read the file
			String line;
			while ((line = reader.readLine()) != null) { // Read each line of the file until the end is reached
				indexLines.add(line); // Add the line to the ArrayList
			}
			reader.close(); // Close the BufferedReader
		} catch (IOException e) {
			e.printStackTrace(); // If an exception occurs, print the stack trace
		}

		// from each stored path in our list we will call the store method recursivley
		// we need to check the type of the files our index file (only .txt files and
		// folders are allowed). We need to do the check because it's not done on add
		// #TODO?
		// if we have a folder it will create the corresponding JGit Folder object and
		// then store it
		// if we have a .txt file it will create the corresponding JGit TextFile and
		// then
		// store it
		for (String filePath : indexLines) {
			// create a system file from the path
			File indexLineFile = new File(filePath);

			if (indexLineFile.isFile() && indexLineFile.getName().endsWith(".txt")) {
				TextFile jGitTextFile = buildJGitTextFile(indexLineFile);
				if (jGitTextFile != null) {
					jGitTextFile.store();
				}
			}

			if (indexLineFile.isDirectory()) {
				Folder jGitFolder = buildJGitFolder(indexLineFile);
				if (jGitFolder != null) {
					jGitFolder.store();
				}

			}
		}

	}

	/**
	 * from a path this function will create the corresponding JGit TextFile with
	 * the right information
	 * 
	 * @param path a string to where our system's .txt file is located
	 * @return the newly built TextFile
	 * @throws WrongFileTypeException
	 */
	public TextFile buildJGitTextFile(File systemTextFile) throws WrongFileTypeException {
		TextFile jGitTextFile = new TextFile();
		if (systemTextFile.isFile() && systemTextFile.getName().endsWith(".txt")) {
			StringBuilder contentBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new FileReader(systemTextFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					contentBuilder.append(line);
					contentBuilder.append(System.lineSeparator());
				}
			} catch (IOException e) {
				System.err.println("Error reading file: " + e.getMessage());
			}
			String content = contentBuilder.toString();
			jGitTextFile.setContent(content);
//			jGitStartingFolder.add(systemFileName, jGitTextFile);
		} else {
			throw new WrongFileTypeException("the file is not a text file");
		}
		return jGitTextFile;
	}

	/**
	 * from a string path this function will create the corresponding JGit folder
	 * with the right information
	 * 
	 * @param path a string to where our system's folder is located
	 * @return the newly built JGit Folder
	 */
	public Folder buildJGitFolder(File systemFolder) {
		return null;
	}

	@Override
	public void execute(String... args) {
		Commit c1 = new Commit();
		c1.addParent(wd.getCurrentCommit());

		String message = args[0];

		c1.setDescription(message);
		// the state of a commit is the state of the project's root folder
		// when we call the commit command we will automatically get
		// the current state of our folder and save it in this commit object
		// we need to add all children files/folders into a new Folder object

		// TODO: change to use results of add()
//		String wdPath = ".";
		String wdPath = wd.getPath().toString();
		Folder jGitRootDir = c1.getState();
		if (jGitRootDir == null) {
			jGitRootDir = new Folder();
		}
		populateWithChildren(wdPath, jGitRootDir);

		c1.setState(jGitRootDir);

		c1.getState().store();

		// store the commit
		c1.store();

		// update the HEAD
		Path filePath = wd.getPath(".jgit", "HEAD");

		StringBuilder content = new StringBuilder();
		for (fr.uca.jgit.model.Commit c : c1.getParents()) {
			if (c != null)
				content.append(c.hash()).append(";");
		}
		if (content.length() > 0)
			content.deleteCharAt(content.length() - 1);
		content.append("\n");
		content.append(LocalTime.now().toString()).append("-").append(LocalDate.now()).append("\n");
		content.append(c1.hash());

		try {
			// update HEAD
			Files.write(filePath, content.toString().getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
			// update the working directory
			wd.addCommit(c1.hash(), c1);
			wd.setCurrentCommit(c1);
		} catch (IOException e) {
			System.out.println("An error occurred while writing to file.");
			e.printStackTrace();
		}
	}

}
