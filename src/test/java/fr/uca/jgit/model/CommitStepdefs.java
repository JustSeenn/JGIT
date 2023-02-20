package fr.uca.jgit.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import fr.uca.jgit.controller.RepositoryController;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class CommitStepdefs {
	Folder folder;
	TextFile file;
	TextFile file2;
	Commit commit1;

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

	@Given("a project folder containing two files containing {string} and {string}")
	public void given(String content1, String content2) throws Throwable {
		RepositoryController.initJGit();

		folder = new Folder();
		file = new TextFile(content1);
		file2 = new TextFile(content2);
		folder.add("file", file);
		folder.add("file2", file2);
	}

	@When("we make a commit with description {string}")
	public void when(String commit) throws Throwable {
		commit1 = new Commit();
		commit1.setState(folder);
		commit1.setDescription("commit1");

		RepositoryController.commit(commit1);

		// check that objects contains the right files and contents
		HashMap<String, String> theoriticalObjects = new HashMap<String, String>();
		theoriticalObjects.put(file.hash(), file.getContent() + "\n");// TODO: check why newline is autoadded?
		theoriticalObjects.put(file.hash(), file.getContent() + "\n");
		theoriticalObjects.put(folder.hash(), folder.toString());

		String folderPath = ".jgit/object";
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

		// check that the HEAD file is updated correctly
		Path HEADPath = Path.of(".jgit/HEAD");

		// Read the contents of the file into a list of strings
		List<String> HEADLines = Files.readAllLines(HEADPath, StandardCharsets.UTF_8);

		assertEquals(commit1.hash(), HEADLines.get(HEADLines.size() - 1));
	}

}
