package fr.uca.jgit.model;

import cucumber.runtime.java.guice.ScenarioScoped;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


@ScenarioScoped
public class WorkingDirectory {
    private Path path;
    private Map<String, JGitObject> commitMap;
    private static WorkingDirectory instance;
    private Commit currentCommit;


    private WorkingDirectory() {
        this.path = null;
        this.commitMap = null;
        this.currentCommit = null;
    }

    public static WorkingDirectory getInstance() {
        if (instance == null) {
            instance = new WorkingDirectory();
        }
        return instance;
    }

    public Path getPath(String... path) {
        return this.path.resolve(Paths.get("", path));
    }

    public TextFile getOriginalFile(String entry){
        return (TextFile) this.currentCommit.getParents().get(0).getState().getChildren().get(entry);
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

}
