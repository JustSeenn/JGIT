package fr.uca.jgit.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Scanner;

public class Folder implements Node {
    // Mapping Name -> Node
    private Map<String, Node> children;

    @Override
    public String hash() {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for(Map.Entry<String, Node> entry : this.children.entrySet()){
                byte[] messageDigest = md.digest(entry.getValue().hash().getBytes());
                for (byte b : messageDigest) {
                    hexString.append(String.format("%02x", b & 0xff));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

    /** Stores the corresponding object in .git directory (file .jgit/object/[hash]) **/
    @Override
    public void store() {
        try {
            String filePath = Paths.get(".jgit", "object", this.hash()).toString();
            File myObj = new File(filePath);
            FileWriter myWriter = new FileWriter(filePath);

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            myWriter.write(this.toString());
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /** Return a list representation of the folder (see doc) **/
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(Map.Entry<String, Node> entry : this.children.entrySet()){
            String td = "t";
            if(entry.getValue() instanceof Folder)
                td = "d";
            str.append(entry.getKey()).append(";").append(td).append(";").append(entry.getValue().hash()).append("\n");
        }
        return str.toString();
    }

    /** Loads the folder corresponding to the given hash (from file .jgit/object/[hash]). **/
    public static Folder loadFolder(String hash) {

            Folder newFolder = new Folder();
            try {
                String filePath = Paths.get(".jgit", "object", hash).toString();
                File myObj = new File(filePath);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String[] line = myReader.nextLine().split(";");
                    newFolder.children.put(line[0], TextFile.loadFile(line[2]));
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            return newFolder;

    }

    /** Restores the file node at the given path. **/
    @Override
    public void restore(String path) {
        try {
            Path newPath = Paths.get(path);
            Files.createDirectories(newPath);
            System.out.println("Directory is created!");

            for (Map.Entry<String, Node> entry : children.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                entry.getValue().restore(path+"/"+entry.getKey());
            }
        } catch (IOException e) {

            System.err.println("Failed to create directory!" + e.getMessage());

        }
    }

    /**
     * Change the current branch to a specified branch
     *
     * @param branchName - name of the branch to check out
     */
    public void changeBranch(String branchName) throws IOException {
        String filePath = Paths.get(".jgit", "refs", "heads", branchName).toString();

        // Check if the branch exists
        File branchFile = new File(filePath);
        if (!branchFile.exists()) {
            System.out.println("Branch " + branchName + " does not exist");
            return ;
        }

        // Get the hash of the head commit
        BufferedReader br = new BufferedReader(new FileReader(branchFile));
        String hash = br.readLine();
        br.close();

        // Load the commit with the given hash
        this.children =  loadFolder(hash).children;
        System.out.println("Switched to a new branch " + branchName);
    }
}
