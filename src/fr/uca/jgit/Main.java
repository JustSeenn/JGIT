package fr.uca.jgit;

import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.TextFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        mergeDemo();
    }

    public static void mergeDemo() throws IOException {
        TextFile file4 = new TextFile("Hello World \nThis is a test ? \nNo it's not \n ");
        TextFile file5 = new TextFile("Hello World ! \nThis is a test ? \nYes it is \n ");

        file4.store();
        file5.store();


        Folder folder1 = new Folder();
        folder1.add("file", file4);

        Folder folder2 = new Folder();
        folder2.add("file", file5);

        Commit commit1 = new Commit();
        commit1.setState(folder1);
        commit1.store();

        Commit commit2 = new Commit();
        commit2.setState(folder2);
        commit2.store();


        Commit c = commit1.merge(commit2);
        Folder newState = c.getState();
        newState.restore(".\\result");
    }
}
