package fr.uca.jgit.command;

import fr.uca.jgit.model.WorkingDirectory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.inject.Inject;

public class CommonStepDefs {
    @Inject
    WorkingDirectory wd;

    @Given("a working directory")
    public void workingDir(){
        Path path = Paths.get("tmpFiles");
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            System.out.println("Error creating directory");
        }
        wd = WorkingDirectory.getInstance();
        wd.setPath(path);
    }
    @Given("a repository .jgit")
    public void a_repository_jgit() {
        Init init = new Init();
        init.execute(String.valueOf(Paths.get(String.valueOf(Path.of(System.getProperty("user.dir"))),"tmpFiles")));
    }
    @Given("a new file named {string} with content {string}")
    public void createFile(String filename, String content){
        try {
            Path filePath = wd.getPath(filename);
            Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @And("we reset the working directory")
    public void weResetTheWorkingDirectory() {
        wd = WorkingDirectory.getInstance();
        try {
            Files.walk(wd.getPath())
                    .sorted((o1, o2) -> o2.compareTo(o1))
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
