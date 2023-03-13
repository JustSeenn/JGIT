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

public class InitStepDefs {

    @Inject
    WorkingDirectory wd;

    @Then("a new jgit repository is created")
    public void a_new_jgit_repository_is_created() {
        Path jgit = WorkingDirectory.getInstance().getPath(".jgit");
        assertTrue(Files.exists(jgit));
        assertTrue(Files.isDirectory(jgit));
        File[] files = jgit.toFile().listFiles();
        assertNotNull(files);
        for (File file : files) {
            if(file.getName().equals("HEAD")){
                assertTrue(file.isFile());
            }
            else if(file.getName().equals("objects")){
                assertTrue(file.isDirectory());
            }
            else if(file.getName().equals("logs")){
                assertTrue(file.isDirectory());
            }
            else{
                fail("Unknown file in .jgit directory: " + file.getName());
            }
        }
        wd = WorkingDirectory.getInstance();
        wd.setPath(Paths.get(".jgit"));
        try {
            Files.walk(wd.getPath())
                    .sorted((o1, o2) -> o2.compareTo(o1))
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wd.setPath(Paths.get("tmpFiles"));
    }

    @Then("no new jgit repository is created")
    public void no_new_jgit_repository_is_created() throws IOException {
        wd = WorkingDirectory.getInstance();
        Path path = Paths.get("tmpFiles");
        wd.setPath(path);
        assertFalse(Files.isDirectory(wd.getPath(".jgit")));
    }
}
    