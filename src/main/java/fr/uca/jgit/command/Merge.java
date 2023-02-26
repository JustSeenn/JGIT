package fr.uca.jgit.command;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.Folder;
import fr.uca.jgit.model.WorkingDirectory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class Merge extends Command {

    public WorkingDirectory wd = WorkingDirectory.getInstance();
    @Override
    public void execute(String... args) {
        // Check if there is a .cl file in the working directory
        Commit c2 = Commit.loadCommit(args[0]);
        File[] files = new File(String.valueOf(wd.getPath())).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(".cl")) {
                        System.out.println("There is a .cl file in the working directory. Please resolve the conflicts before merging.");
                        return;
                    }
                }
            }
        }

        Commit c1 = wd.getCurrentCommit();
        c1.setState((Folder) c1.getState().merge(c2.getState()));
        c1.addParent(c2);
        c1.store();
        StateCommit c3 = new StateCommit();
        c3.execute("Merge commit between " + c1.hash() + " and " + c2.hash());
    }
}
