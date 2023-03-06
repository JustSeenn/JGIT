package fr.uca.jgit.controller;


import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.WorkingDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class RepositoryController {
    /** Get the hash of the last commit from head */
    public static String getHeadHash() {
        String head;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit", "HEAD").toString()));
            head = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                head = line;
            }
            if (head == null) {
                head = "";
            }
        } catch (IOException e) {
            head = "";
        }

        return head;
    }
}
