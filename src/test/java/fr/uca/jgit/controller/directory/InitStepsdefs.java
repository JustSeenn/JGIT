package fr.uca.jgit.controller.directory;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import fr.uca.jgit.model.WorkingDirectory;

import java.nio.file.Files;

import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

public class InitStepsdefs {

    @Then("a new jgit repository is created")
    public void a_new_jgit_repository_is_created() {
        Path jgit = Paths.get(".jgit");
        assertTrue(Files.exists(jgit));
        assertTrue(Files.isDirectory(jgit));
        File[] files = jgit.toFile().listFiles();
        assertNotNull(files);
        for (File file : files) {
            if(file.getName().equals("HEAD")){
                assertTrue(file.isFile());
            }
            else if(file.getName().equals("object")){
                assertTrue(file.isDirectory());
            }
            else if(file.getName().equals("logs")){
                assertTrue(file.isDirectory());
            }
            else{
                fail("Unknown file in .jgit directory: " + file.getName());
            }
        }
        //delete .jgit directory
        File[] filesToDelete = jgit.toFile().listFiles();
        for (File file : filesToDelete) {
            file.delete();
        }
        jgit.toFile().delete();
    }

    @Then("no new jgit repository is created")
    public void no_new_jgit_repository_is_created() throws IOException {
        Path jgit = WorkingDirectory.getInstance().getPath(".jgit");
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
    