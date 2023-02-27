package fr.uca.jgit.command;

import fr.uca.jgit.model.Commit;
import fr.uca.jgit.model.WorkingDirectory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;

public class StateCommit extends Command {

    public WorkingDirectory wd = WorkingDirectory.getInstance();

    @Override
    public void execute(String... args) {

        Commit c1 = WorkingDirectory.getInstance().getCurrentCommit();
        String message = args[0];

        c1.setDescription(message);

        c1.getState().store();

        // store the commit
        c1.store();

        // update the HEAD
        Path filePath = wd.getPath(".jgit", "HEAD");

        StringBuilder content = new StringBuilder();
        for (fr.uca.jgit.model.Commit c : c1.getParents()) {
            content.append(c.hash()).append(";");
        }
        if (content.length() > 0)
            content.deleteCharAt(content.length() - 1);
        content.append("\n");
        content.append(LocalTime.now().toString()).append("-").append(LocalDate.now()).append("\n");
        content.append(c1.hash());

        try {
            Files.write(filePath, content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }
    }
}
