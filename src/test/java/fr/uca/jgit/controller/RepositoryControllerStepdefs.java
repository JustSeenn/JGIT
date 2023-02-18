package fr.uca.jgit.controller;


import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.Node;
import fr.uca.jgit.model.TextFile;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class RepositoryControllerStepdefs {
    TextFile file;
    Folder folder;
    Commit commit1;
    Commit commit2;

    Commit result;



    public RepositoryControllerStepdefs(){}

    @Given("a file named file with the content {string}")
    public void aFile(String content) {
        file = new TextFile(content);
    }

    @Given("a folder named folder1")
    public void aFolder() {
        folder = new Folder();
    }


    @Given("a commit named commit1")
    public void aCommit() {
         commit1 = new Commit();
    }

    @Given("a commit named commit2")
    public void anotherCommit() {
         commit2 = new Commit();
    }

    @When("f1 wants to be merge with f2 and f1 and f2 have the same content")
    public void mergef1SameAsf2() throws IOException {
        folder.add("file", file.clone());
        commit1.setState(folder.clone());

        folder.add("file", file.clone());
        commit2.setState(folder.clone());
        result = RepositoryController.merge(commit1, commit2);
    }

    @Then("the result of the merge has the content {string}")
    public void resultMerge(String content) {
        Folder newState = result.getState();
        // print all the children of newState as a list
        TextFile newFile = (TextFile) newState.getChildren().get("file");
        newFile.setContent(newFile.getContent().replace("\n", " "));
        System.out.println(newFile.getContent() + " " + content);
        assertEquals(newFile.getContent(), content);
    }

    @Then("the result of the merge is a conflict with the content {string}")
    public void resultMergeConflict(String content) {
        Folder newState = result.getState();
        TextFile newFile = (TextFile) newState.getChildren().get("file.cl");
        newFile.setContent(newFile.getContent().replace("\n", " "));
        System.out.println(newFile.getContent() + " " + content);
        assertEquals(newFile.getContent(), content);
    }


    @When("f1 wants to be merge with f2  and f1 modified the file by {string}")
    public void f1WantsToBeMergeWithF2AndF1ModifiedTheFileByAddingTheToBetweenHelloAndWorld(String newContent) throws IOException {
        folder.add("file", file.clone());
        commit1.setState(folder.clone());

        file.setContent(newContent);
        folder.add("file", file.clone());
        commit2.setState(folder.clone());

        result = RepositoryController.merge(commit2, commit1);
    }

    @When("f1 wants to be merge with f2 and f1 delete the first line")
    public void f1WantsToBeMergeWithF2AndF1DeleteTheFirstLine() throws IOException {
        folder.add("file", file.clone());
        commit1.setState(folder.clone());

        file.setContent(file.getContent().replace("Hello World\n", ""));
        folder.add("file", file.clone());
        commit2.setState(folder.clone());

        result = RepositoryController.merge(commit1, commit2);
    }

    @When("f1 wants to be merge with f2  and f1 modified the file by {string} and f2 modified the file by {string}")
    public void f1WantsToBeMergeWithF2AndF1ModifiedTheFileByAddingTheToBetweenHelloAndWorldAndF2ModifiedTheFileByAddingTheToBetweenHelloAndWorld(String newContent1, String newContent2) throws IOException {
        folder.add("file", file.clone());
        commit1.setState(folder.clone());

        file.setContent(newContent1);
        folder.add("file", file.clone());
        commit2.setState(folder.clone());

        result = RepositoryController.merge(commit2, commit1);

        file.setContent(newContent2);
        folder.add("file", file.clone());
        commit2.setState(folder.clone());

        result = RepositoryController.merge(commit2, commit1);
    }





}
