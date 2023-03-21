package fr.uca.jgit.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/*
 * handling the "jgit pull" command server-wise
 */
public class PullRequest {

    List<String> head;
    String commitHash;
    List<String> commitLog;
    List<HashMap<String, List<String>>> objectsList;

    /*
     * Browse the .jgitserver directory and return the list of files
     */
    public void loadPullRequest() {
        head = new ArrayList<>();
        commitLog = new ArrayList<>();
        objectsList = new ArrayList<>();
        try {
            loadHead();
            loadCommitLog();
            //loadObjects();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadObjects() {
        File folder = Path.of(".jgitserver", "objects").toFile();
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

    }

    private void loadCommitLog() {
        try {
            File folder = new File(Path.of(".jgit", "logs").toString());
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    commitLog.addAll(Files.readAllLines(file.toPath()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHead() {
        try {
            head = Files.readAllLines(Path.of(".jgitserver", "HEAD"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> getHead() {
        return head;
    }


    public String getCommitHash() {
        return commitHash;
    }

    public List<String> getCommitLog() {
        return commitLog;
    }
}