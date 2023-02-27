package fr.uca.jgit.controller.directory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AddStepsdefs {

    private File testFile1, testFile2, index;

    WorkingDirectory wd = WorkingDirectory.getInstance();
    
    @Given("an already registered file {string} with content {string}")
    public void anAlreadyRegisteredFileWithContent(String filename, String content) throws IOException {
        index = wd.getPath(".jgit", "index").toFile();
        if(!index.exists()){
            index.createNewFile();
        }

        if(!Files.exists(wd.getPath(filename))){
            testFile2 = Files.createFile(Paths.get(filename)).toFile();
        }
        Files.write(wd.getPath(filename), content.getBytes(), StandardOpenOption.CREATE);
        Files.write(wd.getPath(".jgit", "index"), (content + "\n").getBytes(), StandardOpenOption.APPEND);
        assertTrue(testFile2.exists());
    }
    
    @Then("the index should contain {string} once only")
    public void theOutputShouldContainNothingToAdd(String filename) throws IOException {
        int counter = 0;
        BufferedReader reader = new BufferedReader(new FileReader(index));
        String line = "";
        while ((line = reader.readLine()) != null){
            if (line.toString().contains(filename)) {
                counter++;
            }
        }
        reader.close();
        assertEquals(1, counter);
        if(testFile1 != null) Files.deleteIfExists(wd.getPath(testFile1.getName()));
        if(testFile2 != null) Files.deleteIfExists(wd.getPath(testFile2.getName()));
        Path index = wd.getPath(".jgit", "index");
        if (Files.exists(index)) Files.write(index, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Then("all the files in the directory {string} should be added to the index")
    public void all_the_files_in_the_directory_should_be_added_to_the_index(String path) throws IOException {
        //check if all the files recursively added are in the index
        File workingDirectory = new File(path);
        File[] files = workingDirectory.listFiles();
        if( files != null){
            for (File file : files) {
                if(file.isDirectory()){
                    all_the_files_in_the_directory_should_be_added_to_the_index(file.getPath() + "/");
                }
                if (file.isFile()) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(index));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains(file.getName())) {
                                return;
                            }
                        }
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fail("The file " + file.getName() + " is not in the index.");
                }
            }
        }
        if(testFile1 != null) Files.deleteIfExists(Paths.get(testFile1.getName()));
        if(testFile2 != null) Files.deleteIfExists(Paths.get(testFile2.getName()));
        Path index = wd.getPath(".jgit", "index");
        if (Files.exists(index)) Files.write(index, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
