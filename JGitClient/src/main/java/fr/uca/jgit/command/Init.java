package fr.uca.jgit.command;


import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.WorkingDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;

public class Init extends Command {

    public WorkingDirectory wd = WorkingDirectory.getInstance();
    
    @Override
    public void execute(String... args) {
        try {
            String path = (args.length == 1) ? "." : args[1];
            wd.setPath(Path.of(path));
            Path newPath = wd.getPath(".jgit");
            if(Files.exists(newPath)) {
                System.out.println("There is already a .jgit directory.");
                return;
            }
            Files.createDirectories(newPath);
            newPath = wd.getPath(".jgit", "objects");
            Files.createDirectories(newPath);

            newPath = wd.getPath(".jgit", "logs");
            Files.createDirectories(newPath);

            newPath = wd.getPath(".jgit", "HEAD");
            Files.createFile(newPath);
            System.out.println("Directories are created!");
        } catch (IOException e) {
            System.err.println("Failed to create directories!" + e.getMessage());
        }
    }
}
