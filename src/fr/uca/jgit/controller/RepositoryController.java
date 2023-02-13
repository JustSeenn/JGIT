package fr.uca.jgit.controller;

import fr.uca.jgit.model.Commit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;

public class RepositoryController {
    public static void commit(Commit c1){
        c1.store();

        Path filePath = Paths.get(".jgit", "HEAD");

        StringBuilder content = new StringBuilder();
        for (Commit c : c1.getParents()) {
            content.append(c.hash()).append(";");
        }
        if(content.length() > 0)
            content.deleteCharAt(content.length()-1);
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
