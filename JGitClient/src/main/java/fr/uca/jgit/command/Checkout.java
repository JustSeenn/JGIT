package fr.uca.jgit.command;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.WorkingDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

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

        // Update the current branch information before checkout if the branch is custom branch (not is commit)
        try {
            BufferedReader br = new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit",  "branch_list").toString()));
            List<String> branchList = List.of(br.readLine().split(";"));
            if (branchList.contains(args[0])){
                String head = WorkingDirectory.getInstance().getHeadHash();
                if (!head.isEmpty()){
                    Commit.setAsCurrentBranchState(head);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Error while updating the list of branch. No take this in consideration if you check out a commit");
        }

        // Checkout to given branch
        Commit commit = Commit.loadCommit(args[0]);
        commit.setState(Folder.loadFolder(lastLine));
        Commit.restore(lastLine);
        WorkingDirectory.getInstance().setCurrentCommit(commit);

        // Update current branch name
        try {
            Files.write(WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_"),
                    args[0].getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.out.println("Error while updating current branch name");
            e.printStackTrace();
        }
    }
}