package fr.uca.jgit.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author fayss
 *
 */
public class PushRequest {

	List<String> head;
	String commitHash;
	List<String> commitLog;
	List<HashMap<String, List<String>>> objectsList;

	public PushRequest() {
		commitLog = new ArrayList<>();
		head = new ArrayList<>();
		objectsList = new ArrayList<>();
	}

	public PushRequest(List<String> commitLog, String commitHash, List<String> head,
			List<HashMap<String, List<String>>> objects) {
		serverInit();
		this.commitLog = commitLog;
		this.head = head;
		this.commitHash = commitHash;
		this.objectsList = objects;
	}

/*
 * mimickes the "jgit init" command, creates the .jgitserver folder and its subfolders and HEAD
 */
	public void serverInit() {
		try {
			Files.createDirectory(Path.of(".jgitserver"));
			Files.createDirectories(Path.of(".jgitserver", "objects"));
			Files.createDirectories(Path.of(".jgitserver", "logs"));
			Files.createFile(Path.of(".jgitserver", "HEAD"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * stores the received pushRequest object into the server's .jgit directories
	 * 
	 * @throws IOException
	 */
	public void storePushRequest() throws IOException {
		//checkInit(); // not necessary
		storeHead();
		storeCommitLog();
		storeObjects();
	}

	/**
	 * creates the repositories folders if not already created, if directories
	 * already exist do nothing
	 * 
	 * @throws IOException
	 */
	public void checkInit() throws IOException {
		createFoldersFromPath(Path.of(".jgitserver"));
		createFoldersFromPath(Path.of(".jgitserver", "objects"));
		createFoldersFromPath(Path.of(".jgitserver", "logs"));
	}
	/**
	 * updates the head if the server's head date is less than the pushed commit's
	 * head date.
	 * 
	 * @throws IOException
	 */
	public void storeHead() throws IOException {
		// check for date

		// update head
		storeFile(Path.of(".jgitserver", "HEAD"), head);
	}

	/**
	 * check's if a commit with the same hash already exists, if not store it, else
	 * do nothing
	 * 
	 * @throws IOException
	 */
	public void storeCommitLog() throws IOException {
		// check if commit already exists

		// add commit to logs folder
		storeFile(Path.of(".jgitserver", "logs", commitHash), commitLog);
	}

	public void storeObjects() throws IOException {
		// only keep unstored objects

		// add objects to object folder
		for (HashMap<String, List<String>> objectMap : objectsList) {
			for (Map.Entry<String, List<String>> entry : objectMap.entrySet()) {
				storeFile(Path.of(".jgit", "objects", entry.getKey()), entry.getValue());
			}
		}
	}

	public void storeFile(Path filePathString, List<String> contents) throws IOException {
		// create the path folders if needed
		createFoldersFromPath(filePathString);

		// create and file in the given path
		File textFile = new File(filePathString.toString());
		textFile.createNewFile();

		// fill the file with the given content
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePathString.toString()));
		for (String line : contents) {
			writer.write(line);
			writer.newLine(); // add a newline character after each line
		}
		writer.close();
	}

	/**
	 * creates all the folders that are needed to read the final file of the given
	 * path
	 * 
	 * @param filePathString
	 * @throws IOException
	 */
	public void createFoldersFromPath(Path filePath) throws IOException {
		Path parentPath = Path.of(filePath.getParent().toString());
		if (parentPath != null) {
			Files.createDirectories(parentPath);
		}
	}

	/**
	 * @return the commitLog
	 */
	public List<String> getCommitLog() {
		return commitLog;
	}

	/**
	 * @param commitLog the commitLog to set
	 */
	public void setCommitLog(List<String> commitLog) {
		this.commitLog = commitLog;
	}

	/**
	 * @return the head
	 */
	public List<String> getHead() {
		return head;
	}

	/**
	 * @param head the head to set
	 */
	public void setHead(List<String> head) {
		this.head = head;
	}

	/**
	 * @return the objects
	 */
	public List<HashMap<String, List<String>>> getObjects() {
		return objectsList;
	}

	/**
	 * @param objects the objects to set
	 */
	public void setObjects(List<HashMap<String, List<String>>> objects) {
		this.objectsList = objects;
	}

	/**
	 * @return the commitHash
	 */
	public String getCommitHash() {
		return commitHash;
	}

	/**
	 * @param commitHash the commitHash to set
	 */
	public void setCommitHash(String commitHash) {
		this.commitHash = commitHash;
	}

}
