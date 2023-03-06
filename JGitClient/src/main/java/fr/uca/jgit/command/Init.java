package fr.uca.jgit.command;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import fr.uca.jgit.model.WorkingDirectory;

public class Init extends Command {

    public WorkingDirectory wd = WorkingDirectory.getInstance();
    @Override
    public void execute(String... args) {
        try {
        	String path = (args.length == 1) ? "." : args[1];
            wd.setPath(Path.of(path));
           /* if (Files.exists(Paths.get(".jgit"))) {
                System.out.println("Directories already exist!");
                return;
            }*/
            Path newPath = wd.getPath(".jgit");


            Files.createDirectories(newPath);
            newPath = wd.getPath(".jgit", "object");
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
