package fr.uca.jgit.controller;


import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;

public class RepositoryController {

    public static void initJGit() {
        Path jgit = Paths.get(".jgit");
        Path objects = Paths.get(".jgit", "object");
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


    public static boolean createBranch(String branchName) {
        // Check if the branch exists
        File branchFile = new File(Paths.get(".jgit", "logs", branchName).toString());
        if (branchFile.exists()) {
            System.out.println("fatal: A branch named " + branchName + " already exists.");
            return false;
        }

        String head = RepositoryController.getHeadHash();
        Commit commit;
        if (!head.isEmpty()){
            commit = Commit.loadCommit(head);
        } else {
            // Create a dummy commit to use as a starting point for the branch
            commit  = new Commit();
            commit.setState(new Folder());
            commit.store();
        }
        commit.clone(branchName, false);
        return true;
    }

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

        // Update the current branch information before checkout
        String head = RepositoryController.getHeadHash();
        if (!head.isEmpty()){
            Commit c = Commit.loadCommit(head);
            c.setAsCurrentBranchState();
        }


        // Checkout to given branch
        Commit commit = Commit.loadCommit(hash);
        commit.setState(Folder.loadFolder(lastLine));
        commit.checkout();

        // Update current branch name
        try {
            FileWriter fileWriter = new FileWriter(Paths.get(".jgit", "logs", "_current_branch_").toString(), false);
            fileWriter.write(hash);
        } catch (IOException e) {
            System.out.println("Une erreur est survenue.");
            e.printStackTrace();
        }
    }


    /** Get the hash of the last commit from head */
    private static String getHeadHash() {
        String head;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Paths.get(".jgit", "HEAD").toString()));
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
