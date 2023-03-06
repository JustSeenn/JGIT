package fr.uca.jgit.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Commit implements JGitObject {
	private final List<Commit> parents;
	private Folder state;
	private String description;

	public Commit() {
		this.parents = new ArrayList<>();
		this.state = null;
		this.description = "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Folder getState() {
		return state;
	}

	public List<Commit> getParents() {
		return parents;
	}

	public void addParent(Commit parent) {
		this.parents.add(parent);
	}

	public void setState(Folder folder1) {
		this.state = folder1;
	}

	@Override
	public String hash() {
		String hash = "";
		try {
			// Create a new instance of the MessageDigest using MD5 algorithm
			MessageDigest md = MessageDigest.getInstance("MD5");
			// Convert all the fields of the object to a string
			String data = parents + state.toString() + description;
			// Get the bytes of the string and apply the hash function
			byte[] bytes = md.digest(data.getBytes());
			// Convert the bytes to a hexadecimal string
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				sb.append(String.format("%02x", b));
			}
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	/**
	 * Stores the corresponding object in .git directory (to file .git/logs/[hash]).
	 **/
	@Override
	public void store() {
		try {
			Path filePath = WorkingDirectory.getInstance().getPath(".jgit", "logs", this.hash());
			StringBuilder content = new StringBuilder();
			File myObj = new File(filePath.toString());
			FileWriter myWriter = new FileWriter(filePath.toString());

			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			}
			for (Commit c : parents) {
				if (c != null)
					content.append(c.hash()).append(";");
			}
			if (content.length() > 0)
				content.deleteCharAt(content.length() - 1);
			content.append("\n");
			content.append(" ").append(java.time.LocalTime.now()).append("-").append(java.time.LocalDate.now())
					.append("\n");
			content.append(this.description).append("\n");
			content.append(state.hash());

			myWriter.write(content.toString());
			myWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Loads the commit corresponding to the given hash (from file
	 * .git/logs/[hash]).
	 **/
	public static Commit loadCommit(String hash) {

		Commit newCommit = new Commit();
		try {
			Path filePath = WorkingDirectory.getInstance().getPath(".jgit", "logs", hash);

			File myObj = new File(filePath.toString());

			Scanner myReader = new Scanner(myObj);

			String[] sParents = myReader.nextLine().split(";");
			for (String s : sParents) {
				if (!s.isEmpty()) {
					newCommit.parents.add(Commit.loadCommit(s));
				}
			}
			String temp = "";
			while (myReader.hasNextLine()) {
				temp = myReader.nextLine();
			}
			newCommit.state = Folder.loadFolder(temp);

			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return newCommit;
	}

	/**
	 * Checkout the commit. Removes all working directory content and restores the
	 * state of this commit.
	 **/
	public void checkout() {
		File workingDirectory = new File("result");
		File[] files = workingDirectory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					if (!file.delete()) {
						System.out.println("Error deleting file: " + file.getName());
					}
				}
			}
		}

		this.state.restore("result");

	}

	/**
	 * Duplicate the commit information in the new [fileName] to represent a custom
	 * branch.
	 * 
	 * @param replace - Define if we want to replace [fileName] if the file already
	 *                exist
	 */
	public static void clone(String fileName, String commitHash, boolean replace) {
		FileInputStream input;
		try {
			input = new FileInputStream(
					WorkingDirectory.getInstance().getPath(".jgit", "logs", commitHash).toString());

		} catch (FileNotFoundException e) {
			System.out.println("You must store the commit before clone");
			return;
		}

		FileOutputStream output;
		try {
			File outFile = new File(Paths.get(".jgit", "logs", fileName).toString());
			if (outFile.exists() && !replace) {
				System.out.println("The file " + fileName + "already exist");
				return;
			}
			outFile.createNewFile();
			output = new FileOutputStream(outFile);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			input.close();
			output.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Updates the reference of the current branch to point to this commit
	 */
	public void setAsCurrentBranchState() {
		File branchFile = new File(
				WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_").toString());
		if (!branchFile.exists()) { // It means that no custom branch does not exist
			return;
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(branchFile));
			String current_branch = reader.readLine();
			if (!current_branch.isEmpty()) {
				Commit.clone(current_branch, this.hash(), true);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}