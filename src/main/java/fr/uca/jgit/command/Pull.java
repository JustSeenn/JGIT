package fr.uca.jgit.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import fr.uca.jgit.controller.ServerController;
import fr.uca.jgit.model.PullRequest;

public class Pull extends Command {

    private PullRequest pullRequest;

    @Override
    public void execute(String... args) {
        ServerController serverController = new ServerController();
        pullRequest = serverController.pull().getBody();
        File head = new File(Paths.get(".jgit", "HEAD").toString());
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(head));
		    for (String line : pullRequest.getHead()) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        //TODO: update the local repository with the remote one
    }
}
