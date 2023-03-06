package fr.uca.jgit.command;

import fr.uca.jgit.Main;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckoutStepDefs {
    @Then("the result is {string}")
    public void doNothing(String expected) {
        // Do nothing
    }

    @And("the current branch is not {string}")
    public void theBranchCurrentBranchIsNot(String branchName) {
        Path logsDir = WorkingDirectory.getInstance().getPath(".jgit", "logs");
        Path currentBranchFile = logsDir.resolve("_current_branch_");

        if (Files.notExists(currentBranchFile)) {
            // It's mean, the user don't check out any branch before
            return;
        }

        String currentBranch = null;
        try {
            currentBranch = Files.readString(currentBranchFile).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (currentBranch.equals(branchName)) {
            throw new AssertionError(String.format("Current branch is %s, expected not to be %s", branchName, branchName));
        }
    }

    @Then("the current branch is {string}")
    public void theCurrentBranchIs(String branchName) {
        Path logsDir = WorkingDirectory.getInstance().getPath(".jgit", "logs");
        Path currentBranchFile = logsDir.resolve("_current_branch_");

        if (Files.notExists(currentBranchFile)) {
            // It's mean, the user don't check out any branch before
            throw new AssertionError(String.format("No current branch, expected to be %s", branchName));
        }

        String currentBranch = null;
        try {
            currentBranch = Files.readString(currentBranchFile).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!currentBranch.equals(branchName)) {
            throw new AssertionError(String.format("Current branch is %s, expected to be %s", currentBranch, branchName));
        }
    }
}
