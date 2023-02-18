package fr.uca.jgit.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.uca.jgit.controller.RepositoryController;

/**
 * @author fayss
 *
 */
class CommitTest {

	static Folder folder = new Folder();
	static TextFile textFile = new TextFile("content 1");
	static TextFile textFile2 = new TextFile("content 2");
	static Commit c1 = new Commit();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		folder.add("textFile", textFile);
		folder.add("textFile2", textFile2);
	}

	/**
	 * structure: folder/ |---- file1 |---- file2
	 * 
	 * result: .jgit/ |---- logs |---- commit1Hash
	 * 
	 * |---- objects |---- file1Hash |---- file2Hash |---- folderHash |---- head
	 * :{stateHash}
	 */
	@Test
	void storingCommitTestCase() {
		c1.setState(folder);
		c1.setDescription("First commit");
		RepositoryController.commit(c1);
		// check that objects contains the right files and contents
		// one object for textFile
		// one object for textFile2
		// one object for folder

		// check that the a commit has been added to /.jgit/logs
		// with the correct name and content
		// check that the HEAD file is updated correctly

		// TODO: ...
	}

}
