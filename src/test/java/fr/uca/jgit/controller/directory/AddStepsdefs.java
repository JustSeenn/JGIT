package fr.uca.jgit.controller.directory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.inject.Inject;

import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AddStepsdefs {

    private File testFile1, testFile2, index;

    @Inject
    WorkingDirectory wd;
    
    @Given("the file {string} is already added to the index")
    public void the_file_is_already_added_to_the_index(String s) throws IOException {
        Path jgit = WorkingDirectory.getInstance().getPath(".jgit");
        if(!Files.isDirectory(jgit)){
            Files.deleteIfExists(jgit);
            Files.createDirectory(jgit);
        }
        index = WorkingDirectory.getInstance().getPath(".jgit", "index").toFile();
        if(!index.exists()){
            Files.createFile(WorkingDirectory.getInstance().getPath(".jgit", "index"));
        }
        Files.write(WorkingDirectory.getInstance().getPath(".jgit", "index"), (s + "\n").getBytes(), StandardOpenOption.APPEND);
        assertTrue(index.exists());
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
        if(testFile1 != null) Files.deleteIfExists(WorkingDirectory.getInstance().getPath(testFile1.getName()));
        if(testFile2 != null) Files.deleteIfExists(WorkingDirectory.getInstance().getPath(testFile2.getName()));
        Path index = WorkingDirectory.getInstance().getPath(".jgit", "index");
        if (Files.exists(index)) Files.write(index, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Then("all the files in the directory {string} should be added to the index")
    public void all_the_files_in_the_directory_should_be_added_to_the_index(String path) throws IOException {
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
        Path index = WorkingDirectory.getInstance().getPath(".jgit", "index");
        if (Files.exists(index)) Files.write(index, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
