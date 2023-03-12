package fr.uca.jgit.command;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.WorkingDirectory;

public class Branch extends Command {
    @Override
    public void execute(String... args) {
        String branchName = args[0];

        // Check if the branch exists
        File branchFile = new File(Paths.get(".jgit", "logs", branchName).toString());
        if (branchFile.exists()) {
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
    public static boolean isCustomBranch(String branch) throws IOException {
        List<String> branchList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(WorkingDirectory.getInstance().getPath(".jgit",  "branch_list").toString()));
            branchList = List.of(br.readLine().split(";"));
        } catch (FileNotFoundException e){
            // System.out.println("No branch list file found");
        }
        return branchList.contains(branch);
    }
}
