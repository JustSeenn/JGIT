package fr.uca.jgit.controller.directory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

public class InitStepsdefs {
    @Then("a new jgit repository is created")
    public void a_new_jgit_repository_is_created() {
        //check if the .jgit directory and its files are created
        Path jgit = Paths.get(".jgit");
        assertTrue(Files.exists(jgit));
        Path object = Paths.get(".jgit", "object");
        assertTrue(Files.exists(object));
        Path logs = Paths.get(".jgit", "logs");
        assertTrue(Files.exists(logs));
        Path head = Paths.get(".jgit", "HEAD");
        assertTrue(Files.exists(head));
    }

    @Then("no new jgit repository is created")
    public void no_new_jgit_repository_is_created() throws IOException {
        Path jgit = Paths.get(".jgit");
        if(Files.exists(jgit)){
            if(Files.isDirectory(jgit)){
                File[] files = jgit.toFile().listFiles();
                if(files != null){
                    assertNotEquals(0, files.length);
                }
            }
            else{
                assertNotEquals(0, Files.size(jgit));
            }
        }
    }
}
    