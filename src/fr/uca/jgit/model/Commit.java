package fr.uca.jgit.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Commit implements JGitObject {
    private final List<Commit> parents;
    private Folder state;
    private String description;

    public Commit() {
        this.parents = new ArrayList<>();
        this.state = null;
        this.description = "";
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<Commit> getParents() {
        return parents;
    }

    public void setState(Folder folder1) {
        this.state = folder1;
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
            StringBuilder content = new StringBuilder();
            Path filePath = Paths.get(".jgit", "logs", this.hash());
            File myObj = new File(filePath.toString());
            FileWriter myWriter = new FileWriter(filePath.toString());

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }
            for(Commit c : parents){
                content.append(c.hash()).append(";");
            }
            if(content.length() > 0)
                content.deleteCharAt(content.length()-1);
            content.append("\n");
            content.append(" ").append(java.time.LocalTime.now()).append("-").append(java.time.LocalDate.now()).append("\n");
            content.append(this.description).append("\n");
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
            Path filePath = Paths.get(".jgit", "logs", hash);
            File myObj = new File(filePath.toString());

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

    Commit merge(Commit other) throws IOException {
        Commit newCommit = new Commit();
        newCommit.state = (Folder) state.merge(other.state);
        newCommit.parents.add(this);
        newCommit.parents.add(other);
        return newCommit;
    }


}
