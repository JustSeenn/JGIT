package fr.uca.jgit.controller;


import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.WorkingDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class RepositoryController {
    public static void initJGit() {
        Path jgit = Paths.get(".jgit");
        Path objects = Paths.get(".jgit", "objects");
        Path logs = Paths.get(".jgit", "logs");
        Path head = Paths.get(".jgit", "HEAD");
        try {
            if(Files.exists(jgit) &&
                    Files.exists(objects) &&
                    Files.exists(logs) &&
                    Files.exists(head)){
                System.out.println("Directories already exist!");
                return;
            }
            if (!Files.exists(jgit)) {
                Files.createDirectories(jgit);
            }
            if (!Files.exists(objects)) {
                Files.createDirectories(objects);
            }
            if (!Files.exists(logs)) {
                Files.createDirectories(logs);
            }
            if (!Files.exists(jgit)) {
                Files.createFile(head);
            }
            System.out.println("Directories are created!");
        } catch (IOException e) {
            System.err.println("Failed to create directories!" + e.getMessage());
        }
    }

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
