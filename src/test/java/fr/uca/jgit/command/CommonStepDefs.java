package fr.uca.jgit.command;

import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.inject.Inject;

public class CommonStepDefs {
    @Inject
    WorkingDirectory wd;

    @Given("a working directory")
    public void workingDir(){
        Path path = Paths.get("tmpFiles");
        try {
            Files.createDirectory(path);
            wd = WorkingDirectory.getInstance();
            wd.setPath(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @And("a new file named {string} with content {string}")
    public void createFile(String filename, String content){
        try {
            Path filePath = wd.getPath(filename);
            Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("the result is")
    public void doNothing(){}
}
