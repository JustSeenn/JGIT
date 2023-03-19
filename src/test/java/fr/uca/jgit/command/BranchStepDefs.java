package fr.uca.jgit.command;

import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.nio.file.Path;
import java.nio.file.Files;

public class BranchStepDefs {
    @Then("the branch {string} does not created")
    public void branchNotCreated(String branchName){
        Path branchFile = WorkingDirectory.getInstance().getPath(".jgit", "logs",branchName);

        if (Files.exists(branchFile)) {
            throw new AssertionError(String.format("Branch '%s' should not exist", branchName));
        }
    }

    @And("the branch {string} is create")
    public void theBranchIsCreate(String branchName) {
        Path branchFile = WorkingDirectory.getInstance().getPath(".jgit", "logs",branchName);

        if (!Files.exists(branchFile)) {
            throw new AssertionError(String.format("Branch '%s' should exist", branchName));
        }
    }
}
