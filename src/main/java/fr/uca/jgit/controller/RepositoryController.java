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
    public static void commit(Commit c1){
        c1.store();

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
        try {
            if(Files.exists(Paths.get(".jgit"))){
                System.out.println("Directories already exist!");
                return;
            }
            Path newPath = Paths.get(".jgit");
            Files.createDirectories(newPath);
            newPath = Paths.get(".jgit", "object");
            Files.createDirectories(newPath);
            newPath = Paths.get(".jgit", "logs");
            Files.createDirectories(newPath);
            newPath = Paths.get(".jgit", "HEAD");
            Files.createFile(newPath);
            System.out.println("Directories are created!");
        } catch (IOException e) {
            System.err.println("Failed to create directories!" + e.getMessage());
        }
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

        Commit commit = Commit.loadCommit(hash);
        commit.setState(Folder.loadFolder(lastLine));
        commit.checkout();

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
}
