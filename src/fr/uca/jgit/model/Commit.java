package fr.uca.jgit.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;

public class Commit implements JGitObject {
    private final List<Commit> parents;
    private Folder state;

    public Commit(){
        this.parents = new ArrayList<>();
        this.state = null;
    }

    public void setState(Folder state){
        this.state = state;
    }

    public Folder getState(){
        return this.state;
    }

    @Override
    public String hash() {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for(Commit entry : this.parents){
                byte[] messageDigest = md.digest(entry.hash().getBytes());
                for (byte b : messageDigest) {
                    hexString.append(String.format("%02x", b & 0xff));
                }
            }
            byte[] messageDigest = md.digest(state.hash().getBytes());
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b & 0xff));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

    /** Stores the corresponding object in .git directory (to file .git/logs/[hash]). **/
    @Override
    public void store() {
        try {
            Path filePath = Paths.get(".jgit", "logs", this.hash());
            StringBuilder content = new StringBuilder();
            File myObj = new File(filePath.toString());
            FileWriter myWriter = new FileWriter(filePath.toString());

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            for(Commit c : parents){
                content.append(c.hash()).append(";");
            }
            if (content.length() > 0)
                content.deleteCharAt(content.length()-1);
            content.append("\n");
            content.append(" ").append(java.time.LocalTime.now()).append("-").append(java.time.LocalDate.now()).append("\n");
            // append commit message ?
            content.append(state.hash());

            myWriter.write(content.toString());
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }



    /** Loads the commit corresponding to the given hash (from file .git/logs/[hash]). **/
    public static Commit loadCommit(String hash) {

        Commit newCommit = new Commit();
        try {
            File myObj = new File(".git/logs/"+hash);
            Scanner myReader = new Scanner(myObj);

            String[] sParents = myReader.nextLine().split(";");
            for(String s: sParents){
                newCommit.parents.add(Commit.loadCommit(s));
            }
            String temp = "";
            while(myReader.hasNextLine()){
                temp = myReader.nextLine();
            }
            newCommit.state = Folder.loadFolder(temp);

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return newCommit;
    }

    /** Checkout the commit.
     * Removes all working directory content and restores the state of this commit.  **/
    void checkout() {
        File workingDirectory = new File(".");
        File[] files = workingDirectory.listFiles();
        if( files != null){
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        System.out.println("Error deleting file: " + file.getName());
                    }
                }
            }
        }

        this.state.restore("");

    }

    public Commit merge(Commit other) throws IOException {
        // Check if there is a .cl file in the working directory
        File workingDirectory = new File(".");
        File[] files = workingDirectory.listFiles();
        if( files != null){
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(".cl")) {
                        System.out.println("There is a .cl file in the working directory. Please resolve the conflicts before merging.");
                        return null;
                    }
                }
            }
        }
        Commit newCommit = new Commit();
        newCommit.state = (Folder) state.merge((other).state);
        newCommit.parents.add(this);
        newCommit.parents.add(other);

        // Store the new commit
        newCommit.store();

        // Update the file .jgit/HEAD
        Path filePath = Paths.get(".jgit", "HEAD");

        StringBuilder content = new StringBuilder();
        for (Commit c : newCommit.parents) {
            content.append(c.hash()).append(";");
        }
        if(content.length() > 0)
            content.deleteCharAt(content.length()-1);
        content.append("\n");
        content.append(LocalTime.now().toString()).append("-").append(LocalDate.now()).append("\n");
        content.append(newCommit.hash());

        try {
            Files.write(filePath, content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }


        return newCommit;
    }
}
