package fr.uca.jgit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import fr.uca.jgit.controller.RepositoryController;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AddStepsStepsdefs {

    private File testFile1, testFile2, index;
    private String output;

    @Before
    public void init() throws IOException {
        if(!Files.exists(Paths.get(".jgit"))){
            RepositoryController.initJGit();
        }

        index = Paths.get(".jgit", "index").toFile();
        if(!index.exists()){
            index.createNewFile();
        }
    }

    @Given("a jgit repository with a new file {string} with content {string}")
    public void aRepositoryWithFile(String filename, String content) throws IOException {
        if(!Files.exists(Paths.get(filename))){
            testFile1 = Files.createFile(Paths.get(filename)).toFile();
        }
        Files.write(Paths.get(filename), content.getBytes(), StandardOpenOption.CREATE);
        assertTrue(testFile1.exists());
    }

    @Given("an already registered file {string} with content {string}")
    public void anAlreadyRegisteredFileWithContent(String filename, String content) throws IOException {
        if(!Files.exists(Paths.get(filename))){
            testFile2 = Files.createFile(Paths.get(filename)).toFile();
        }
        Files.write(Paths.get(filename), content.getBytes(), StandardOpenOption.CREATE);
        Files.write(Paths.get(".jgit", "index"), (filename + "\n").getBytes(), StandardOpenOption.APPEND);
        assertTrue(testFile2.exists());
    }

    @When("I add {string} to the index")
    public void iAddToTheIndex(String filename) {
//        output = RepositoryController.add(filename);
    }

    @When("I add all the files to the index")
    public void iAddAllTheFilesToTheIndex() {
//        output = RepositoryController.add(".");
    }

    @When("{string} is already in the index")
    public void isAlreadyInTheIndex(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(index));
        StringBuilder line = new StringBuilder();
        while (line.append(reader.readLine()) != null){
            if (line.toString().contains(path)) {
                break;
            }
        }
        reader.close();
        assertTrue(index.length() > 0);
        assertNotNull(line);
        assertTrue(line.toString().contains(path));
    }
    
    @Then("the output should contain {string} about {string}")
    public void theOutputShouldContainNothingToAdd(String output, String filename) throws IOException {
        assertEquals(output, this.output);
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

    @Then("the file[s] {string} should be added to the index with the output {string}")
    public void theFileIsAddedToTheIndex(String filename, String output) throws IOException {
        assertTrue(index.length() > 0);
        BufferedReader reader = new BufferedReader(new FileReader(index));
        StringBuilder line = new StringBuilder();
        while (line.append(reader.readLine()) != null){
            if (line.toString().contains(filename)) {
                break;
            }
        }
        reader.close();
        assertTrue(line.toString().contains(filename));
        assertEquals(output, this.output);
    }

    @Then("the file[s] {string} should not be added a second time to the index")
    public void the_file_s_should_not_be_added_a_second_time_to_the_index(String filename) throws IOException {
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

    @After
    public void cleanUp() throws IOException {
        if(testFile1 != null) Files.deleteIfExists(Paths.get(testFile1.getName()));
        if(testFile2 != null) Files.deleteIfExists(Paths.get(testFile2.getName()));
        Path index = Paths.get(".jgit", "index");
        if (Files.exists(index)) Files.write(index, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
