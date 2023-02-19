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

import org.junit.jupiter.api.Test;

import fr.uca.jgit.controller.RepositoryController;

/**
 * @author fayss
 *
 */
class CommitTest {
	
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

	static Folder folder = new Folder();
	static TextFile textFile = new TextFile("content 1");
	static TextFile textFile2 = new TextFile("content 2");
	static Commit c1 = new Commit();
	

	/**
	 * structure: 
	 * 	folder/ 
	 * 		|---- file1 
	 * 		|---- file2
	 * 
	 * result: 
	 * 	.jgit/ 
	 * 		|---- logs 
	 * 			|---- commit1Hash
	 * 		|---- objects 
	 * 			|---- file1Hash 
	 * 			|---- file2Hash 
	 * 			|---- folderHash 
	 * 		|---- head
	 * :{stateHash}
	 * @throws IOException 
	 */
	@Test
	void storingCommitTestCase() throws IOException {
		RepositoryController.initJGit();
		folder.add("textFile", textFile);
		folder.add("textFile2", textFile2);
		
		c1.setState(folder);
		c1.setDescription("First commit");
		RepositoryController.commit(c1);
		// check that objects contains the right files and contents
		HashMap<String, String> theoriticalObjects = new HashMap<String, String>();
		theoriticalObjects.put(textFile.hash(), textFile.getContent()+"\n" );// TODO: check why newline is autoadded?
		theoriticalObjects.put(textFile2.hash(), textFile2.getContent()+"\n" );
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
        
        assertTrue(commitsMap.keySet().contains(c1.hash()));
        assertEquals(commitsMap.get(c1.hash()).get(commitsMap.get(c1.hash()).size()-1), c1.getState().hash());// check repo hash
        
        
		// check that the HEAD file is updated correctly
        Path HEADPath = Path.of(".jgit/HEAD");

        // Read the contents of the file into a list of strings
        List<String> HEADLines = Files.readAllLines(HEADPath, StandardCharsets.UTF_8);
        
        assertEquals(c1.hash(), HEADLines.get(HEADLines.size() - 1));
	}


}
