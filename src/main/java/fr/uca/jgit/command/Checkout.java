package fr.uca.jgit.command;

import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.WorkingDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;

public class Checkout extends Command{
    @Override
    public void execute(String... args) {
        if (args.length == 0){
            System.out.println("Argument required");
            return;
        }

        // Check if the branch exists
        File branchFile = new File(WorkingDirectory.getInstance().getPath(".jgit", "logs", args[0]).toString());
        if (!branchFile.exists()) {
            System.out.println("Branch " + args[0] + " does not exist");
            return;
        }

        // Update the current branch information before checkout if the branch is custom branch (not is commit)
        try {
            Path _current_branch_ = WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_");
            if (Files.exists(_current_branch_)){
                String currentBranchName = (new BufferedReader(new FileReader(_current_branch_.toString()))).readLine().trim();

                if (Branch.isBranch(currentBranchName)){
                    String head = WorkingDirectory.getInstance().getHeadHash();
                    if (!head.isEmpty()){
                        Path currentBranchPath = WorkingDirectory.getInstance().getPath(".jgit", "logs", currentBranchName);
                        Files.writeString(currentBranchPath,
                                head,
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
                        );
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Warning: Error while updating the current branch information.");
            e.printStackTrace();
        }

        // Checkout to given branch
        Commit commit = Commit.loadCommit(Branch.toHash(args[0]));
        Commit.restore(Branch.toHash(args[0]));
        WorkingDirectory.getInstance().setCurrentCommit(commit);

        // Update current branch name
        try {
            Path current_branch = WorkingDirectory.getInstance().getPath(".jgit", "logs", "_current_branch_");
            if (Branch.isBranch(args[0])){
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
        headContent.append(Branch.toHash(args[0]));

        try {
            Files.write(headPath, headContent.toString().getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("An error occurred while updating the head file.");
            e.printStackTrace();
        }
    }
}
