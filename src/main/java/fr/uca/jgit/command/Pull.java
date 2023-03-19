package fr.uca.jgit.command;

import fr.uca.jgit.controller.ServerController;

public class Pull extends Command {
    @Override
    public void execute(String... args) {
        ServerController serverController = new ServerController();
        serverController.pull();
    }
}
