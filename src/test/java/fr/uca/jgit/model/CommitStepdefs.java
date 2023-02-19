package fr.uca.jgit.model;

import java.util.ArrayList;
import java.util.List;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CommitStepdefs {
	List<Folder> folders = new ArrayList<Folder>();
	List<TextFile> textFiles = new ArrayList<TextFile>();
	
  @Given("Given a project folder containing the folder {string}")
  public void given(String folderName) throws Throwable {
	  Folder folder = new Folder();
  }
  
  @And("And inside {string} a file {string} containing {string}")
  public void and(String folder, String file, String content) throws Throwable {
	  
  }

  @When("^you are in When annotation$")
  public void when() throws Throwable {
  }

  @Then("^you are in Then annotation$")
  public void then() throws Throwable {
  }

}
