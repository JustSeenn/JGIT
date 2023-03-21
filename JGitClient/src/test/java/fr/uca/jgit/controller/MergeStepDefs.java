package fr.uca.jgit.controller;


import fr.uca.jgit.command.Add;
import fr.uca.jgit.command.Merge;
import fr.uca.jgit.command.StateCommit;
import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.TextFile;

import javax.inject.Inject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class MergeStepDefs {
    TextFile file;
    Folder folder;
    Commit commit1;
    Commit commit2;

    @Inject
    WorkingDirectory wd;



    public MergeStepDefs(){}


    @Given("a file named file with the content {string}")
    public void a_file_named_file_with_the_content(String content) {
        file = new TextFile(content);
    }


    @Given("a folder named folder1")
    public void a_folder_named_folder1() {
        folder = new Folder();
    }


    @Given("a commit named commit1 which is the current commit")
    public void a_commit_named_commit1_which_is_the_current_commit() {

        Add add = new Add();
        StateCommit st = new StateCommit();
        st.execute("commit1");
    }

    @Given("a commit named commit2")
    public void a_commit_named_commit2() {
        commit2 = new Commit();
    }

    @When("the content of commit2 is {string}")
    public void the_content_of_commit2_is(String content) {
        Folder f = new Folder();
        f.add("file", new TextFile(content));

        commit2.setState(f);
        commit2.addParent(commit1);
        WorkingDirectory.getInstance().setCurrentCommit(commit2);
        StateCommit st = new StateCommit();
        st.execute("commit2");
    }

    @When("the content of commit3 is {string}")
    public void the_content_of_commit3_is(String content) {
        Commit commit3 = new Commit();
        Folder f = new Folder();
        f.add("file", new TextFile(content));

        commit3.setState(f.clone());
        commit3.addParent(commit1);

        WorkingDirectory.getInstance().setCurrentCommit(commit3);
        StateCommit st = new StateCommit();
        st.execute("commit3");


    }

    @When("I do the command git merge {string}")
    public void i_do_the_command_git_merge(String arg0) {
        Merge m = new Merge();
        m.execute(arg0);
    }

    @When("we modify the content of file.txt with {string}")
    public void we_modify_the_content_of_file_txt_with_hello_the_world(String content) throws IOException {
        String filePath = "tmpFiles\\file.txt";
        FileWriter file = new FileWriter(filePath);
        file.write(content);
        System.out.println("Successfully wrote to the file.");
        file.close();
    }
    @Then("The result of the merge has the content {string}")
    public void the_result_of_the_merge_has_the_content(String content) {
        // get the file.txt in tmpFiles/...
        File f = new File("tmpFiles\\file.txt.cl");
        if(!f.exists()){
            f = new File("tmpFiles\\file.txt");
        }
        // get the content of the file f
        String contentFile = "";
        try {
            contentFile = new String(java.nio.file.Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }



        System.err.println(contentFile.replace("\n", "") + "  |-|  " + content);
        assertEquals(contentFile.replace("\n", ""), content);
    }
}
