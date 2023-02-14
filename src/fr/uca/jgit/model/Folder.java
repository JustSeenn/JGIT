package fr.uca.jgit.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Folder implements Node {
    // Mapping Name -> Node
    private Map<String, Node> children;

    public Folder(){
        children = new HashMap<>();
    }
    public void add(String name, Node node){
        this.children.put(name, node);
    }
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

    /** Stores the corresponding object in .git directory (file .git/object/[hash]) **/
    @Override
    public void store() {
        try {
            String filePath = Paths.get(".jgit", "object", this.hash()).toString();
            File myObj = new File(filePath);
            FileWriter myWriter = new FileWriter(filePath);

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
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

    /** Loads the folder corresponding to the given hash (from file .git/object/[hash]). **/
    public static Folder loadFolder(String hash) {

            Folder newFolder = new Folder();
            try {
                File myObj = new File(Paths.get(".jgit", "object", hash).toString());
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

    public static void initJGit() {
        try {
            Path newPath = Paths.get(".jgit");
            Files.createDirectories(newPath);
            newPath = Paths.get(".jgit", "object");
            Files.createDirectories(newPath);
            newPath = Paths.get(".jgit", "logs");
            Files.createDirectories(newPath);
            newPath = Paths.get(".jgit", "HEAD");
            Files.createFile(newPath);
            System.out.println("Directories are created!");
        } catch (IOException e) {
            System.err.println("Failed to create directories!" + e.getMessage());
        }
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

    @Override
    public Node merge(Node other) throws IOException {
        Folder newFolder = new Folder();
        for (Map.Entry<String, Node> entry : children.entrySet()) {
            if(other instanceof Folder){
                if(((Folder) other).children.containsKey(entry.getKey())){
                    TextFile temp = (TextFile) entry.getValue().merge(((Folder) other).children.get(entry.getKey()));
                    if(temp.getContent().contains("<<<<<<<"))
                        newFolder.children.put(entry.getKey() + ".cl", temp);
                    else
                        newFolder.children.put(entry.getKey(), temp);
                }else {
                    newFolder.children.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, Node> entry : ((Folder) other).children.entrySet()) {
            if(!newFolder.children.containsKey(entry.getKey()) && !newFolder.children.containsKey(entry.getKey() + ".cl") ){
                newFolder.children.put(entry.getKey(), entry.getValue());
            }


        }


        return newFolder;

    }

    public void changeBranch(String hash) throws IOException {
        // Check if the branch exists
        File branchFile = new File(Paths.get(".jgit", "logs", hash).toString());
        if (!branchFile.exists()) {
            System.out.println("Branch " + hash + " does not exist");
            return ;
        }

        // Get the hash for the state of repo
        String lastLine = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(branchFile));
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Failed to read the last line of the file.");
            e.printStackTrace();
        }

        // Restore working directory
        Commit commit = Commit.loadCommit(hash);
        commit.checkout();
        this.children = loadFolder(lastLine).children;
    }

    public Folder clone(){
        Folder f = new Folder();
        for(Map.Entry<String, Node> entry : this.children.entrySet()){
            f.add(entry.getKey(), entry.getValue());
        }

        return f;
    }
}
