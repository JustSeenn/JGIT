package fr.uca.jgit.controller.directory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.inject.Inject;

import fr.uca.jgit.Main;
import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AddStepsdefs {

    private File index;

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
        Files.write(WorkingDirectory.getInstance().getPath(".jgit", "index"), (WorkingDirectory.getInstance().getPath(s) + "\n").getBytes(), StandardOpenOption.APPEND);
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
    }

    @Then("all the files in the directory {string} should be added to the index")
    public void all_the_files_in_the_directory_should_be_added_to_the_index(String path) throws IOException {
        if(path.equals(".")) path = "";
        File addedDirectory = new File(WorkingDirectory.getInstance().getPath(path).toString());
        System.out.println("tmp: " + WorkingDirectory.getInstance().getPath(path));
        File[] files = addedDirectory.listFiles();
        if( files != null){
            for (File file : files) {
                if(file.isDirectory()){
                    all_the_files_in_the_directory_should_be_added_to_the_index(file.getPath());
                }
                if (file.isFile()) {
                    BufferedReader reader = new BufferedReader(new FileReader(index));
                    String line = "";
                    boolean found = false;
                    while ((line = reader.readLine()) != null){
                        System.out.println("line: " + line.toString() + " file: " + file.toPath().toString());
                        if (line.toString().contains(file.toPath().toString())) {
                            found = true;
                        }
                    }
                    reader.close();
                    assertTrue(found, "File " + file.toPath().toString() + " not found in index");
                }
            }
        }
    }
}
