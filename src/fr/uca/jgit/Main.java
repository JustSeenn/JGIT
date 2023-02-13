package fr.uca.jgit;

import fr.uca.jgit.model.Folder;

public class Main {

    public static void main(String[] args) {
        try {
            Folder.initJGit();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
