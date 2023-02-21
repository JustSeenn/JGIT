package fr.uca.jgit.controller.branch;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.TextFile;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckoutStepDefs {
    TextFile textFile, textFile2;
    Folder folder;
    Commit commit1, commit2;

    @Given("a text file named textFile with the content {string}")
    public void aTextFile(String content) {
        textFile = new TextFile(content);
        textFile.store();
    }
    @And("a text file named textFile2 with the content {string}")
    public void anotherTextFile(String content) {
        textFile2 = new TextFile(content);
        textFile2.store();
    }

    @And("a folder named folder with textFile as new child")
    public void aFolder() {
        folder = new Folder();
        folder.add("file 1", textFile);
        folder.store();
    }

    @And("a folder named folder with textFile2 as new child")
    public void aFolderWithSecondChild(){
        folder.add("file 2", textFile2);
        folder.store();
    }

    @And("a commit named commit1 with folder as his state")
    public void aCommit() {
        commit1 = new Commit();
        commit1.setState(folder.clone());
        commit1.setDescription("First commit");
        commit1.store();
        System.out.println("Right now " + commit1.hash());
    }

    @And("a commit named commit2 with folder as his state")
    public void anotherCommit() {
        commit2 = new Commit();
        commit2.setState(folder.clone());
        commit2.setDescription("Second commit");
        commit2.addParent(commit1);
        commit2.store();
    }

    @When("checkout a commit 1")
    public void checkoutCommit1(){
        System.out.println("Switch to branch " + commit1.hash() + " : ");
        RepositoryController.changeBranch(commit1.hash());
    }

    @Then("the folder contain one file")
    public void checkoutCommit1Result(){

    }

    @When("checkout a commit 2")
    public void checkoutCommit2(){
        System.out.println("Switch to branch " + commit2.hash() + " : ");
        RepositoryController.changeBranch(commit2.hash());
    }

    @Then("the folder contain two files")
    public void checkoutCommit1Result2(){

    }

    @When("the user want to checkout a branch named {string}")
    public void checkout(String name) {
        RepositoryController.changeBranch(name);
    }

    @Then("the result of the checkout")
    public void checkoutResult(){
        //
    }
}
