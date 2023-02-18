package fr.uca.jgit.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextFile implements Node {
	private String content;

	/** For dev purpose **/
	public TextFile(String content) {
		this.content = content;
	}

	public TextFile() {
		this.content = "";
	}

	public String getContent() {
		return this.content;
	}

	@Override
	public String hash() {
		StringBuilder hexString = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(this.content.getBytes());

			for (byte b : messageDigest) {
				hexString.append(String.format("%02x", b & 0xff));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hexString.toString();
	}

	/**
	 * Stores the corresponding object in .git directory (to file
	 * .git/object/[hash]).
	 **/
	@Override
	public void store() {
		try {
			Path filePath = Paths.get(".jgit", "object", this.hash());
			FileWriter myWriter = new FileWriter(filePath.toString());

			myWriter.write(this.content);
			myWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Loads the text file corresponding to the given hash (from file
	 * .git/object/[hash]).
	 **/
	public static TextFile loadFile(String hash) {
		StringBuilder content = new StringBuilder();
		try {
			Path filePath = Paths.get(".jgit", "object", hash);

			File myObj = new File(filePath.toString());
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				content.append(myReader.nextLine());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		TextFile newTextFile = new TextFile();
		newTextFile.content = content.toString();
		return newTextFile;
	}

	/** Restores the file node at the given path. **/
	@Override
	public void restore(String path) {
		try {
			File myObj = new File(path);
			FileWriter myWriter = new FileWriter(path);

			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			}
			myWriter.write(this.content);
			myWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/** Merges the given file with this file. **/
	@Override
	public Node merge(Node other) {
		TextFile mergeFile = new TextFile();
		TextFile otherFile = (TextFile) other;
		List<String> file1Lines = List.of(this.content.split("\n"));
		List<String> file2Lines = List.of(otherFile.content.split("\n"));
		List<String> ls = mergeFiles(file1Lines, file2Lines);
		for (String s : ls) {
			mergeFile.content += s + "\n";
		}
		return mergeFile;
	}

	public static List<String> mergeFiles(List<String> file1, List<String> file2) {
		List<String> merged = new ArrayList<>();
		int[][] dp = new int[file1.size() + 1][file2.size() + 1];
		for (int i = 0; i <= file1.size(); i++) {
			dp[i][0] = i;
		}
		for (int j = 0; j <= file2.size(); j++) {
			dp[0][j] = j;
		}
		for (int i = 1; i <= file1.size(); i++) {
			for (int j = 1; j <= file2.size(); j++) {
				if (file1.get(i - 1).equals(file2.get(j - 1))) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + 1;
				}
			}
		}
		int i = file1.size();
		int j = file2.size();
		while (i > 0 || j > 0) {
			if (i > 0 && j > 0 && file1.get(i - 1).equals(file2.get(j - 1))) {
				merged.add(0, file1.get(i - 1));
				i--;
				j--;
			} else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
				merged.add(0, "<<<<<<< HEAD");
				merged.add(0, file1.get(i - 1));
				merged.add(0, "=======");
				i--;
			} else {
				merged.add(0, file2.get(j - 1));
				j--;
			}
		}
		while (i > 0) {
			merged.add(0, "<<<<<<< HEAD");
			merged.add(0, file1.get(i - 1));
			merged.add(0, "=======");
			i--;
		}
		while (j > 0) {
			merged.add(0, "<<<<<<< HEAD");
			merged.add(0, file2.get(j - 1));
			merged.add(0, "=======");
			j--;
		}
		return merged;
	}
}
