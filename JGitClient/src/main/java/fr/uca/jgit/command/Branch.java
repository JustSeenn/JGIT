package fr.uca.jgit.command;

import java.io.File;
import java.nio.file.Paths;
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

        try {
            String head = WorkingDirectory.getInstance().getHeadHash();
            if (!head.isEmpty()){
                Commit.clone(branchName, head, false);
                System.out.println("Branch " + branchName + " created");
            } else {
                System.out.println("fatal: Not a valid object in 'HEAD'");
            }
        } catch (Exception e) {
            System.out.println("fatal: Not a valid object in 'HEAD'");
        }

    }
}
