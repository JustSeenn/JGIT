package fr.uca.jgit.command;

import fr.uca.jgit.model.WorkingDirectory;

import javax.inject.Inject;
import java.nio.file.Path;

public abstract class Command {
    @Inject
    WorkingDirectory wd;
    public Command() {
        wd = WorkingDirectory.getInstance();
    }


    public abstract void execute(String... args);

}
