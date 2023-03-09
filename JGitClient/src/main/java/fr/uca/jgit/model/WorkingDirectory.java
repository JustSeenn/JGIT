package fr.uca.jgit.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import cucumber.runtime.java.guice.ScenarioScoped;


@ScenarioScoped
public class WorkingDirectory {
    private Path path;
    private Map<String, JGitObject> commitMap;
    private static WorkingDirectory instance;
    private Commit currentCommit;


    private WorkingDirectory() {
        this.path = null;
        this.commitMap = new HashMap<>();
        this.currentCommit = null;
    }

    public static WorkingDirectory getInstance() {
        if (instance == null) {
            instance = new WorkingDirectory();
        }
        return instance;
    }

    public Path getPath(String... path) {
        // Use the user.dir when the working directory path isn't defined
        if (this.path == null) {
            this.path = Paths.get(".");
        }

        return this.path.resolve(Paths.get("", path));
    }


    public Map<String, JGitObject> getCommitMap() {
        return commitMap;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void addCommit(String hash, JGitObject commit) {
        commitMap.put(hash, commit);
    }

    public void setCommitMap(Map<String, JGitObject> commitMap) {
        this.commitMap = commitMap;
    }

    public void setCurrentCommit(Commit commit) {
        this.currentCommit = commit;
    }

    public Commit getCurrentCommit() {
        return this.currentCommit;
    }

    public void reset() {
        this.currentCommit = null;
        this.commitMap = null;
        this.path = null;
    }

    /** Get the hash of the last commit from head */
    public String getHeadHash() {
        String head;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit", "HEAD").toString()));
            head = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                head = line;
            }
            if (head == null) {
                head = "";
            }
        } catch (IOException e) {
            head = "";
        }

        return head;
    }
}
