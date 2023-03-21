package fr.uca.jgit.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import fr.uca.jgit.controller.ServerController;
import fr.uca.jgit.model.PushRequest;

public class Push extends Command {

    PushRequest pushRequest;

    List<String> head;
    String commitHash;
    List<String> commitLog;
    List<HashMap<String, List<String>>> objectsList;
    
    @Override
    public void execute(String... args) {
        head = new ArrayList<>();
        commitLog = new ArrayList<>();
        objectsList = new ArrayList<>();

        File folder = new File(Path.of(".jgit", "objects").toString());
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    HashMap<String, List<String>> object = new HashMap<>();
                    object.put(file.getName(), lines);
                    objectsList.add(object);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            folder = new File(Path.of(".jgit", "logs").toString());
            listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    commitHash = file.getName(); // TODO: check if it's the right way to choose the commitHash
                    commitLog.addAll(Files.readAllLines(file.toPath()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            head = Files.readAllLines(Path.of(".jgit", "HEAD"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pushRequest = new PushRequest(head, commitHash, commitLog, objectsList);
        ServerController serverController = new ServerController();
        try {
            serverController.push(pushRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
