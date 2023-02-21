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
    public static void commit(Commit c1){
		// store the repository's directory
		c1.getState().store();
		
		// store the commit
        c1.store();

        // update the HEAD
        Path filePath = Paths.get(".jgit", "HEAD");

        StringBuilder content = new StringBuilder();
        for (Commit c : c1.getParents()) {
            content.append(c.hash()).append(";");
        }
        if(content.length() > 0)
            content.deleteCharAt(content.length()-1);
        content.append("\n");
        content.append(LocalTime.now().toString()).append("-").append(LocalDate.now()).append("\n");
        content.append(c1.hash());

        try {
            Files.write(filePath, content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }

    }

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
            return ;
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

    public static Commit merge(Commit c1, Commit c2) throws IOException {
        // Check if there is a .cl file in the working directory
        File workingDirectory = new File(".");
        File[] files = workingDirectory.listFiles();
        if( files != null){
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(".cl")) {
                        System.out.println("There is a .cl file in the working directory. Please resolve the conflicts before merging.");
                        return null;
                    }
                }
            }
        }
        Commit newCommit = new Commit();
        newCommit.setState((Folder) c1.getState().merge(c2.getState()));
        newCommit.addParent(c1);
        newCommit.addParent(c2);
        newCommit.setDescription("Merge commit between " + c1.hash() + " and " + c2.hash());
        RepositoryController.commit(newCommit);


        return newCommit;
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

    public static String add(String path){
        if(path.endsWith("/") || path.endsWith(".")){
            File workingDirectory = new File(path);
            File[] files = workingDirectory.listFiles();
            if( files != null){
                for (File file : files) {
                    if(file.isDirectory()){
                        add(file.getPath() + "/");
                    }
                    if (file.isFile()) {
                        add(file.getName());
                    }
                }
            }
            return "The files in the directory " + path + " have been added to the index.";
        }
        File workingDirectory = new File(".");
        File[] files = workingDirectory.listFiles();
        if( files != null){
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(path)) {
                        // Check if the file is already in the index
                        File indexFile = new File(Paths.get(".jgit", "index").toString());
                        if (!indexFile.exists()) {
                            try {
                                Files.createFile(Paths.get(".jgit", "index"));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return "An error occurred while creating the index file.";
                            }
                        }
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(indexFile));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.contains(path)) {
                                    return "The file " + path + " is already in the index.";
                                }
                            }
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "Failed to read the index file.";
                        }

                        // Add the file to the index
                        try {
                            Files.write(Paths.get(".jgit", "index"), (path + "\n").getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "An error occurred while writing to file.";
                        }
                        return "The file " + path + " has been added to the index.";
                    }
                }
            }
        }
        return "The file " + path + " does not exist.";
    }
}
