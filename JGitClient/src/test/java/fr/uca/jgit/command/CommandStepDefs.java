package fr.uca.jgit.command;

import fr.uca.jgit.Main;
import io.cucumber.java.en.When;

import java.io.IOException;

public class CommandStepDefs {
    String[] args;

    public CommandStepDefs() {
        this.args = new String[]{"", ""};
    }

    @When("the user execute the command {string}")
    @When("the user want to execute the command {string}")
    public void executeCommandWithoutArgs(String command){
        executeCommand(command, "");
    }

    @When("the user execute the command {string} with argument {string}")
    @When("the user want to execute the command {string} with argument {string}")
    public void executeCommand(String command, String argument) {
        try {
            args[0] = command;
            args[1] = argument;
            Main.main(args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
