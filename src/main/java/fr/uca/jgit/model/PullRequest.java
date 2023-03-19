package fr.uca.jgit.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import fr.uca.jgit.util.Util;	

/*
 * handling the "jgit pull" command server-wise
 */
public class PullRequest {

    List<String> head;
	String commitHash;
	List<String> commitLog;
	List<HashMap<String, List<String>>> objectsList;

    public PullRequest() {
        commitLog = new ArrayList<>();
        head = new ArrayList<>();
        objectsList = new ArrayList<>();
    }

    public PullRequest(List<String> commitLog, String commitHash, List<String> head,
            List<HashMap<String, List<String>>> objects) {
        this.commitLog = commitLog;
        this.head = head;
        this.commitHash = commitHash;
        this.objectsList = objects;
    }

    /**
     * loads the pullRequest object from the server's .jgit directories
     * 
     * @throws IOException
     */
    public void loadPullRequest() throws IOException {
        loadHead();
        loadCommitLog();
        loadObjects();
    }

    /**
     * loads the head file from the server's .jgit directories
     * 
     * @throws IOException
     */
    public void loadHead() throws IOException {
        head = readFile(Path.of(".jgitserver", "HEAD").toString());
    }

    /**
     * loads the commitLog file from the server's .jgit directories
     * 
     * @throws IOException
     */
    public void loadCommitLog() throws IOException {
        commitLog = readFile(Path.of(".jgitserver", "logs", "commitLog").toString());
    }

    /**
     * loads the objects from the server's .jgit directories
     * 
     * @throws IOException
     */
    public void loadObjects() {
        String path = Path.of(".jgitserver", "objects").toString();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        //do smth
    }

    public List<String> readFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines = Files.readAllLines(Path.of(path));
        return lines;
    }

    
}
