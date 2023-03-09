package fr.uca.jgit.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class CommitStepdefs {
	Folder folder;
	TextFile file;
	TextFile file2;
	Commit commit1;

	WorkingDirectory wd = WorkingDirectory.getInstance();

	public CommitStepdefs() {

	}

	private static String readFileContents(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	@And("a temporary .txt file {string} containing {string}")
	public void createFileWithPath(String filePathString, String content) {
		try {
			// create needed dirs
			Path filePath = Path.of(filePathString);
			Path parentPath = Path.of(".tmp/" + filePath.getParent());
			if (parentPath != null) {
				Files.createDirectories(parentPath);
			}

			// create .txt file
			File textFile = new File(".tmp/" + filePathString);
			textFile.createNewFile();

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

	@Then("the object folder contains the right hashed files with the right content")
	public void the_jgit_object_folder_contains_the_right_hashed_files_with_the_right_content() throws Throwable {
		// check that objects contains the right files and contents
		HashMap<String, String> theoriticalObjects = new HashMap<String, String>();
		theoriticalObjects.put(file.hash(), file.getContent() + "\n");// TODO: check why newline is autoadded?
		theoriticalObjects.put(file.hash(), file.getContent() + "\n");
		theoriticalObjects.put(folder.hash(), folder.toString());

		String folderPath = ".jgit/objects";
		File folder = new File(folderPath);
		File[] files = folder.listFiles();

		HashMap<String, String> filesMap = new HashMap<>();
		for (File file : files) {
			if (file.isFile()) {
				String contents = readFileContents(file);
				filesMap.put(file.getName(), contents);
			}
		}

		assertTrue(filesMap.keySet().containsAll(theoriticalObjects.keySet()));
		assertTrue(filesMap.values().containsAll(theoriticalObjects.values()));
	}

	@And("a hashed commit file has been added to the .jgit\\/logs folder containing the right info")
	public void a_hashed_commit_file_has_been_added_to_the_jgit_logs_folder_containing_the_right_info()
			throws Throwable {
		// check that the a commit has been added to /.jgit/logs
		String logsFolderPath = ".jgit/logs";
		File logsFolder = new File(logsFolderPath);
		File[] commits = logsFolder.listFiles();

		HashMap<String, List<String>> commitsMap = new HashMap<>();
		for (File file : commits) {
			if (file.isFile()) {
				List<String> contents = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
				commitsMap.put(file.getName(), contents);
			}
		}

		assertTrue(commitsMap.keySet().contains(commit1.hash()));
		assertEquals(commitsMap.get(commit1.hash()).get(commitsMap.get(commit1.hash()).size() - 1),
				commit1.getState().hash());// check repo hash
	}

	@And("the HEAD file has been correctly updated")
	public void headUpdated() throws Throwable {
		// check that the HEAD file is updated correctly
		Path HEADPath = Path.of(".jgit/HEAD");

		// Read the contents of the file into a list of strings
		List<String> HEADLines = Files.readAllLines(HEADPath, StandardCharsets.UTF_8);

		assertEquals(commit1.hash(), HEADLines.get(HEADLines.size() - 1));

	}

}
