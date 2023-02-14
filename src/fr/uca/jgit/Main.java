package fr.uca.jgit;
import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.TextFile;

import java.io.IOException;

public class Main {

    public static void main(String[] args){
        try {
            Folder.initJGit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //mergeDemo();
        //demoCommit();
        checkoutDemo();
    }
    public static void demoCommit() {
        TextFile file4 = new TextFile("Hello World \nThis is a test ? \nNo it's not \n ");
        Folder folder1 = new Folder();
        folder1.add("file4", file4);
        Commit c1 = new Commit();
        c1.setState(folder1);
        c1.setDescription("First commit");
        RepositoryController.commit(c1);

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
        commit1.setDescription("First commit");
        commit1.store();


        Commit commit2 = new Commit();
        commit2.setState(folder2);
        commit2.setDescription("Second commit");
        commit2.store();


        Commit c = commit1.merge(commit2);

        Folder newState = c.getState();
        newState.restore("result");
    }

    public static void checkoutDemo(){
        // Branch change test


        TextFile textFile = new TextFile("First file");

        textFile.store();

        Folder folder1 = new Folder();
        folder1.add("file 1", textFile);
        folder1.store();

        Commit commit1 = new Commit();
        commit1.setState(folder1.clone());
        commit1.setDescription("First commit");
        commit1.store();
        System.out.println( "Right now " + commit1.hash());

        TextFile textFile2 = new TextFile("Second file");
        textFile2.store();

        folder1.add("file 2", textFile2);
        folder1.store();

        Commit commit2 = new Commit();
        commit2.setState(folder1.clone());
        commit2.setDescription("Second commit");
        commit2.addParent(commit1);
        commit2.store();

        System.out.println("Switch to branch " + commit1.hash() + " : ");
        RepositoryController.changeBranch(commit1.hash());

        System.out.println("Switch to branch " + commit2.hash() + " : ");
        RepositoryController.changeBranch(commit2.hash());

        System.out.println("Switch to branch " + commit1.hash() + " : ");
        RepositoryController.changeBranch(commit1.hash());
    }
}
