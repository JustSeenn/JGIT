package fr.uca.jgit.model;

import java.io.IOException;

public interface Node extends JGitObject {
    /** Restores the file node at the given path. **/
    void restore(String path);

    /** Change the current branch to a specified branch */
    void changeBranch(String branchName) throws IOException;
}
