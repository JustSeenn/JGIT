package fr.uca.jgit;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.TextFile;

import java.io.IOException;

public class Main {

    public static void main(String[] args){
        demoCommit();
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
}
