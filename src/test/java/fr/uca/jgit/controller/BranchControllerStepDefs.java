package fr.uca.jgit.controller;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.File;

import static org.junit.Assert.*;

public class BranchControllerStepDefs {
    boolean result1, result2;

    @Before
    public void init(){
        // Remove previous tacking
        File folder = new File(".jgit");
        this.deleteFolder(folder);

        RepositoryController.initJGit();
    }

    @When("the user want to create a new branch named dev")
    public void createBranchDev(){
        result1 = RepositoryController.createBranch("dev");
    }

    @Then("the branch is created")
    public void createNewBranchSuccess(){
        assertTrue(result1);
    }

    @When("the user want to create a branch named dev again")
    public void createBranchDevAgain(){
        result2 = RepositoryController.createBranch("dev");
    }

    @Then("the branch the branch named dev does not create again")
    public void createBranchSuccessAndFail(){
        assertTrue(result1);
        assertFalse(result2);
    }

    private void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
    }
}
