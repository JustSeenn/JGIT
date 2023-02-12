package fr.uca.jgit;

import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.TextFile;

import java.io.IOException;

public class Main {

    public static void main(String[] args){

        TextFile file4 = new TextFile("Hello World \nThis is a test ? \nNo it's not \n ");
        TextFile file5 = new TextFile("Hello World ! \nThis is a test ? \nYes it is \n ");
        TextFile file6 = (TextFile) file4.merge(file5);
        System.out.println(file6.content);
        file6.store();

    }
}
