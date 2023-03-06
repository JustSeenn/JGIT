package fr.uca.jgit.command;

import com.sun.jdi.request.DuplicateRequestException;
import fr.uca.jgit.model.WorkingDirectory;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Add extends Command{



    @Override
    public void execute(String... args) {
        if(args.length == 0){
            System.out.println("No path specified.");
            return;
        }
        String path = args[0];
        if(path.endsWith("/") || path.endsWith(".")){
            File workingDirectory = new File(path);
            File[] files = workingDirectory.listFiles();
            if( files != null){
                for (File file : files) {
                    if(file.isDirectory()){
                        execute(file.getPath() + "/");
                    }
                    if (file.isFile()) {
                        execute(file.getName());
                    }
                }
            }
            return;
        }
        File[] files = new File(super.wd.getPath().toString()).listFiles();

        if( files != null){
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(path)) {
                        // Check if the file is already in the index
                        File indexFile = new File(super.wd.getPath(".jgit", "index").toString());
                        if (!indexFile.exists()) {
                            try {
                                Files.createFile(super.wd.getPath(".jgit", "index"));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(indexFile));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.contains(path)) {
                                    throw new DuplicateRequestException("The file " + path + " is already in the index.");
                                }
                            }
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Add the file to the index
                        try {
                            Files.write(Paths.get(".jgit", "index"), (path + "\n").getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        }
    }
}
