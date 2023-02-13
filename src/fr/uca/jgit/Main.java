package fr.uca.jgit;

import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.TextFile;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // Branch change test
        TextFile textFile = new TextFile("First commit");
        textFile.store();
        Commit.createBranch("step-1", textFile.hash());
        textFile.setContent(textFile.getContent() + "\n A new line \n New Content");
        textFile.store();
        Commit.createBranch("step-2", textFile.hash());

        textFile.changeBranch("step-1");
        System.out.println("The content of the file on the branch step-1 is \n --> " + textFile.getContent() + "<-- \n");
        textFile.changeBranch("step-2");
        System.out.println("The content of the file on the branch step-2 is \n --> " + textFile.getContent() + "<-- \n");
        textFile.changeBranch("step-1");
        System.out.println("The content of the file on the branch step-1 is \n --> " + textFile.getContent() + "<-- \n");
    }
}
