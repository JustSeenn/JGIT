package fr.uca.jgit.command;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import fr.uca.jgit.model.WorkingDirectory;

public class Branch extends Command {
    @Override
    public void execute(String... args) {
        if (args.length == 0){
            System.out.println("Argument required");
            return;
        }

        String branchName = args[0];

        // Check if the branch exists
        if (isBranch(branchName)) {
            System.out.println("fatal: A branch named " + branchName + " already exists.");
            return;
        }

        // Update a list of branch
        try {
            Files.write(WorkingDirectory.getInstance().getPath(".jgit", "branch_list"),
                    (args[0]+";").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.out.println("Error while updating the list of branch");
            e.printStackTrace();
        }

        try {
            String head = WorkingDirectory.getInstance().getHeadHash();
            if (!head.isEmpty()){
                Path current_branch = WorkingDirectory.getInstance().getPath(".jgit", "logs", branchName);
                Files.writeString(current_branch,
                        head,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
                );

                System.out.println("Branch " + branchName + " created");
            } else {
                System.out.println("fatal: Not a valid object in 'HEAD'");
            }
        } catch (Exception e) {
            System.out.println("fatal: Not a valid object in 'HEAD'");
        }

    }

    /**
     * Check if the given branch is a current branch
     * @param branch is the name of the branch. Can be hash or name
     * @return true if the current branch is not a simple commit else false
     */
    public static boolean isBranch(String branch) {
        List<String> branchList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit",  "branch_list").toString()));
            branchList = List.of(br.readLine().split(";"));
        } catch (IOException ignored){}
        return branchList.contains(branch);
    }


    /**
     * Transform a given name into a hash
     *
     * @param name The name of a branch or the hash of a commit
     * @return the commit corresponding to name
     */
    public static String toHash(String name){
        // Get object corresponding to the commit
        try {
            if (!Branch.isBranch(name)){
                return name;
            } else {
                return (new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit", "logs", name).toString()))).readLine().trim();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
