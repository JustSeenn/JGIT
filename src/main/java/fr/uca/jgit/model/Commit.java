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
import java.util.*;

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
            ArrayList<String> contentFile = new ArrayList<>();
            while (myReader.hasNextLine()) {
                contentFile.add(myReader.nextLine());
            }
            newCommit.description = contentFile.get(contentFile.size() - 2);
            newCommit.state = Folder.loadFolder(contentFile.get(contentFile.size() - 1));

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
	 * Restore the state of the files at the given commit
	 *
	 * Follow the state of the commit recursively from the current commit [hash]
	 * to the first parent and restore the last state of each file.
	 *
	 * @param hash the hash of the commit to restore
	 */
	public static void restore(String hash){
		// todo: restore recursively by including the parent state
		// todo: checkout (remove file)

		// Get the hash for the state of repo
		String lastLine = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(WorkingDirectory.getInstance().getPath(".jgit", "logs", hash).toString())));
			String line;
			while ((line = reader.readLine()) != null) {
				lastLine = line;
			}
			reader.close();
		} catch (Exception e) {
			System.err.println("Failed to read the last line of the file.");
			e.printStackTrace();
		}

		String fileName, objectName;
		Map<String, String> children = new HashMap<>(); // list of <Filename, Corresponding_object>
		TextFile tmpFile;
		try {
			BufferedReader br = new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit", "objects", lastLine).toString()));

			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(";");
				fileName = tokens[0].trim();
				objectName = tokens[2].trim();
				children.put(fileName, objectName);

				// restore the file
				tmpFile = TextFile.loadFile(objectName);
				tmpFile.restore(fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
