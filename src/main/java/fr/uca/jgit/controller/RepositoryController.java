package fr.uca.jgit.controller;


import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;

public class RepositoryController {

    public static void changeBranch(String hash) {
        // Check if the branch exists
        File branchFile = new File(Paths.get(".jgit", "logs", hash).toString());
        if (!branchFile.exists()) {
            System.out.println("Branch " + hash + " does not exist");
            return;
        }

        // Get the hash for the state of repo
        String lastLine = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(branchFile));
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Failed to read the last line of the file.");
            e.printStackTrace();
        }

        Commit commit = Commit.loadCommit(hash);
        commit.setState(Folder.loadFolder(lastLine));
        commit.checkout();
    }
}
