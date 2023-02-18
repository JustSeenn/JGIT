package fr.uca.jgit.model;

import java.io.IOException;

public interface Node extends JGitObject {
	/** Restores the file node at the given path. **/
	void restore(String path);

	Node merge(Node other) throws IOException;
}
