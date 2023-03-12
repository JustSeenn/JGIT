package fr.uca.jgit.command;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.WorkingDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
            String currentBranch = (new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_").toString()))).readLine().trim();

            if (isCustomBranch(currentBranch)){
                String head = WorkingDirectory.getInstance().getHeadHash();
                if (!head.isEmpty()){
                    Commit.setAsCurrentBranchState(head);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Error while updating the list of branch. No take this in consideration if you check out a commit");
            e.printStackTrace();
        }

        // Checkout to given branch
        Commit commit = Commit.loadCommit(args[0]);
        commit.setState(Folder.loadFolder(lastLine));
        Commit.restore(lastLine);
        WorkingDirectory.getInstance().setCurrentCommit(commit);

        // Update current branch name
        try {
            Path current_branch = WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_");
            if (isCustomBranch(args[0])){
                Files.write(current_branch,
                        args[0].getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
                );
            } else {
                if (Files.exists(current_branch)) Files.delete(WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_"));
            }
        } catch (IOException e) {
            System.out.println("Error while updating current branch name " + args[0]);
            e.printStackTrace();
        }
        
        // Update the head
        Path headPath = WorkingDirectory.getInstance().getPath(".jgit", "HEAD");

        StringBuilder headContent = new StringBuilder();
        headContent.append(LocalTime.now().toString()).append("-").append(LocalDate.now()).append("\n");
        try {
            if (! isCustomBranch(args[0])){
                headContent.append(args[0]);
            } else {
                // todo(fix): the hash of branch instead of name
            }
        } catch (IOException e) {
            headContent.append(args[0]);
        }

        try {
            Files.write(headPath, headContent.toString().getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("An error occurred while updating the head file.");
            e.printStackTrace();
        }
    }

    /**
     * Check if the current branch is a current branch
     * @return true if the current branch is not a simple commit else false
     */
    private boolean isCustomBranch(String branch) throws IOException {
        List<String> branchList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit",  "branch_list").toString()));
            branchList = List.of(br.readLine().split(";"));
        } catch (FileNotFoundException e){
            System.out.println("No branch list file found");
        }
        return branchList.contains(branch);
    }
}
