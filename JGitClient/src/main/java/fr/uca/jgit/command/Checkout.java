package fr.uca.jgit.command;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.WorkingDirectory;

import java.io.*;
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
        // Update the current branch information before checkout
        String head = RepositoryController.getHeadHash();
        if (!head.isEmpty()){
            Commit c = Commit.loadCommit(head);
            c.setAsCurrentBranchState();
        }

        // Checkout to given branch
        Commit commit = Commit.loadCommit(args[0]);
        commit.setState(Folder.loadFolder(lastLine));
        commit.checkout();
        WorkingDirectory.getInstance().setCurrentCommit(commit);

        // Update current branch name
        try {
            FileWriter fileWriter = new FileWriter(WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_").toString(), false);
            fileWriter.write(args[0]);
        } catch (IOException e) {
            System.out.println("Error while updating current branch name");
            e.printStackTrace();
        }
    }
}
