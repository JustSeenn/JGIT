package fr.uca.jgit.model;

import cucumber.runtime.java.guice.ScenarioScoped;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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

    public Commit getCommonFather(Commit c2){
        // Create a list that travel the commit tree and add all the node to the list
        List<Commit> listCurrentParents = new ArrayList<>(this.currentCommit.getParents());
        while (listCurrentParents.get(listCurrentParents.size()-1).getParents().size() != 0){
            listCurrentParents.add(listCurrentParents.get(listCurrentParents.size()-1).getParents().get(0));
        }

        List<Commit> listCurrentParents2 = new ArrayList<>(c2.getParents());
        if(listCurrentParents2.size() == 0){
            return listCurrentParents.get(0);
        }
        while (listCurrentParents2.get(listCurrentParents2.size()-1).getParents().size() != 0){
            listCurrentParents2.add(listCurrentParents2.get(listCurrentParents2.size()-1).getParents().get(0));
        }

        // Compare the two list and return the first common node
        for (Commit commit : listCurrentParents) {
            for (Commit commit2 : listCurrentParents2) {
                if (Objects.equals(commit.hash(), commit2.hash())){
                    return commit;
                }
            }
        }

        return null;
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
