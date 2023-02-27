package fr.uca.jgit.command;

import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;

public class Checkout extends Command{
    @Override
    public void execute(String... args) {
        // Check if the branch exists
        File branchFile = new File(Paths.get(".jgit", "logs", args[0]).toString());
        if (!branchFile.exists()) {
            System.out.println("Branch " + args[0] + " does not exist");
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

        // Checkout to given branch
        Commit commit = Commit.loadCommit(args[0]);
        commit.setState(Folder.loadFolder(lastLine));
        commit.checkout();
        super.wd.setCurrentCommit(commit);
    }
}
