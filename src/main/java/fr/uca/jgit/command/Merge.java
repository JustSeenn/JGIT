package fr.uca.jgit.command;

import fr.uca.jgit.controller.RepositoryController;
import fr.uca.jgit.model.*;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

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
        Folder toto = (Folder) c1.getState().merge(c2.getState());
        c1.setState(toto);
        Add add = new Add();

        for (Map.Entry<String, Node> entry : toto.getChildren().entrySet()) {
            String filename = entry.getKey();
            String content = ((TextFile) toto.getChildren().get(filename)).getContent();
            File file = new File(filename);
            add.execute(file.getName());
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StateCommit c3 = new StateCommit();
        c3.execute("Merge commit between " + c1.hash() + " and " + args[0]);


        File workingDirectory = new File(wd.getPath("result").toString() );
        File[] tmpfiles = workingDirectory.listFiles();
        if (tmpfiles != null) {
            for (File file : tmpfiles) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        System.out.println("Error deleting file: " + file.getName());
                    }
                }
            }
        }

        c1.getState().restore(wd.getPath("result").toString());
    }
}
