package fr.uca.jgit.command;

import fr.uca.jgit.model.WorkingDirectory;

import javax.inject.Inject;
import java.nio.file.Path;

public abstract class Command {
    @Inject
    WorkingDirectory wd;

    public Command() {
        wd = WorkingDirectory.getInstance();
        wd.setPath(Path.of(System.getProperty("user.dir")));
    }


    public abstract void execute(String... args);

}
