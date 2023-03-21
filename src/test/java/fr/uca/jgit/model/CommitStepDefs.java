package fr.uca.jgit.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class CommitStepDefs {

	public CommitStepDefs() {

	}



	@And("a temporary .txt file {string} containing {string}")
	public void createTxtFileWithPath(String filePathString, String content) {
		try {
			// create needed dirs
			Path filePath = Path.of(filePathString);
			Path parentPath = null;
			if (filePath.getParent() != null) {
				parentPath = Path.of("tmpFiles", filePath.getParent().toString());
			}
			if (parentPath != null) {
				Files.createDirectories(parentPath);
			}

			// create .txt file
			File textFile = new File("tmpFiles/" + filePathString);

			// set .txt file contents
			try {
				FileWriter fileWriter = new FileWriter(textFile);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

				bufferedWriter.write(content);

				bufferedWriter.close();

				System.out.println("Content set successfully.");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@And("a temporary .java file {string} containing {string}")
	public void createJavaFileWithPath(String filePathString, String content) throws IOException {
		Path tmpDir = WorkingDirectory.getInstance().getPath(filePathString);

		// Création du fichier
		File file = new File(tmpDir.toString());

		// create needed dirs
		Path filePath = Path.of(filePathString);
		Path parentPath = null;
		if (filePath.getParent() != null) {
			parentPath = Path.of("tmpFiles", filePath.getParent().toString());
		}
		if (parentPath != null) {
			Files.createDirectories(parentPath);
		}

		// create .txt file
		File textFile = new File("tmpFiles/" + filePathString);


		// Écriture du contenu dans le fichier
		try {
			FileWriter writer = new FileWriter(textFile);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@Then("the object folder contains the right hashed files with the right content")
	public void the_jgit_object_folder_contains_the_right_hashed_files_with_the_right_content() throws Throwable {

		List<String> expectedHashes = Arrays.asList("9a0364b9e99bb480dd25e1f0284c8555", "2835fd650ae017013e94b81528f449e5", "6685cd62b95f2c58818cb20e7292168b", "9297ab3fbd56b42f6566284119238125");
		for (String expectedHash : expectedHashes) {
			Path hashedFile = WorkingDirectory.getInstance().getPath(".jgit","objects", expectedHash);
			assertTrue(Files.exists(hashedFile));
			String content = Files.readString(hashedFile);
			switch (expectedHash) {
				case "9a0364b9e99bb480dd25e1f0284c8555" -> assertEquals("content", content);
				case "9297ab3fbd56b42f6566284119238125" -> assertEquals("content 1", content);
				case "2835fd650ae017013e94b81528f449e5" -> {
					String expectedContent = """
							tmpFiles\\file.txt;t;9a0364b9e99bb480dd25e1f0284c8555
							tmpFiles\\dir1\\file.txt;t;9a0364b9e99bb480dd25e1f0284c8555
							tmpFiles\\dir1\\dir2\\file.java;t;9297ab3fbd56b42f6566284119238125
							tmpFiles\\dir1\\dir2\\file2.txt;t;6685cd62b95f2c58818cb20e7292168b
							""";
					assertEquals(expectedContent, content);
				}
				case "6685cd62b95f2c58818cb20e7292168b" -> assertEquals("content 2", content);
			}
		}
	}



	@And("a hashed commit file has been added")
	public void a_hashed_commit_file_has_been_added()
			throws Throwable {
		Path commitFile = WorkingDirectory.getInstance().getPath(".jgit", "logs", "7948987f6e4f18e39e867d4b3848893b");
		assertTrue(Files.exists(commitFile));
		List<String> commitFileLines = Files.readAllLines(commitFile);
		assertEquals(4, commitFileLines.size());
		assertEquals("", commitFileLines.get(0));
		assertEquals("commit msg", commitFileLines.get(2));
		assertEquals("2835fd650ae017013e94b81528f449e5", commitFileLines.get(3));


	}

	@And("the HEAD file has been correctly updated")
	public void headUpdated() throws Throwable {
		Path headFile = WorkingDirectory.getInstance().getPath(".jgit", "HEAD");
		assertTrue(Files.exists(headFile));
		List<String> commitFileLines = Files.readAllLines(headFile);
		assertEquals(3, commitFileLines.size());
		assertEquals("", commitFileLines.get(0));
		assertEquals("7948987f6e4f18e39e867d4b3848893b", commitFileLines.get(2));

	}

}
