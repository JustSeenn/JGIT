package fr.uca.jgit.command;

import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.Assert.assertTrue;

public class CheckoutStepDefs {
    @And("the current branch is not {string}")
    public void theBranchCurrentBranchIsNot(String branchName) {
        Path logsDir = WorkingDirectory.getInstance().getPath(".jgit", "logs");
        Path currentBranchFile = logsDir.resolve("_current_branch_");

        if (Files.notExists(currentBranchFile)) {
            assertTrue(true);
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
