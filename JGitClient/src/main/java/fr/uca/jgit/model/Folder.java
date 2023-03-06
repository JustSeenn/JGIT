package fr.uca.jgit.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Folder implements Node {
    // Mapping Name -> Node
    private final Map<String, Node> children;

    public Folder() {
        children = new HashMap<>();
    }

    public void add(String name, Node node) {
        this.children.put(name, node);
    }

    public Map<String, Node> getChildren() {
        return children;
    }

    @Override
    public String hash() {
        String hash = "";
        try {
            // Create a new instance of the MessageDigest using MD5 algorithm
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Get the values of the map in a sorted order
            List<Node> values = new ArrayList<>(children.values());
            // TODO: sort this list to avoid different hashs for same folder structure

            // Convert the hashes of the children to a string
            StringBuilder sb = new StringBuilder();
            for (Node node : values) {
                sb.append(node.hash()); // recursively call hash() on each child
            }
            String data = sb.toString();

            // Get the bytes of the string and apply the hash function
            byte[] bytes = md.digest(data.getBytes());

            // Convert the bytes to a hexadecimal string
            sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * Stores the corresponding object in .git directory (file .git/object/[hash])
     **/
    @Override
    public void store() {
        try {
            // start off by storing the children first
            // Get the values of the map in a sorted order
            List<Node> values = new ArrayList<>(children.values());
            for (Node node : values) {
                node.store();
            }

            // then store the current folder object
            String filePath = WorkingDirectory.getInstance().getPath(".jgit", "object", this.hash()).toString();


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

    /**
     * Return a list representation of the folder (see doc)
     **/
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Node> entry : this.children.entrySet()) {
            String td = "t";
            if (entry.getValue() instanceof Folder)
                td = "d";
            str.append(entry.getKey()).append(";").append(td).append(";").append(entry.getValue().hash()).append("\n");
        }
        return str.toString();
    }

    /**
     * Loads the folder corresponding to the given hash (from file .git/object/[hash]).
     **/
    public static Folder loadFolder(String hash) {

        Folder newFolder = new Folder();
        try {
            File myObj = new File(WorkingDirectory.getInstance().getPath(".jgit", "object", hash).toString());
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

    /**
     * Restores the file node at the given path.
     **/
    @Override
    public void restore(String path) {
        try {
            Path newPath = Paths.get(path);
            Files.createDirectories(newPath);
            System.out.println("Directory is created!");

            for (Map.Entry<String, Node> entry : children.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());

                entry.getValue().restore(Paths.get(path, entry.getKey()).toString());
            }
        } catch (IOException e) {

            System.err.println("Failed to create directory!" + e.getMessage());

        }
    }

    @Override
    public Node merge(Node other) {
        Folder newFolder = new Folder();
        for (Map.Entry<String, Node> entry : children.entrySet()) {
            if (other instanceof Folder) {
                if (((Folder) other).children.containsKey(entry.getKey())) {
                    TextFile temp = (TextFile) entry.getValue().merge(((Folder) other).children.get(entry.getKey()));
                    temp.store();
                    if (temp.getContent().contains("<<<<<<<"))
                        newFolder.children.put(entry.getKey() + ".cl", temp);
                    else
                        newFolder.children.put(entry.getKey(), temp);
                } else {
                    newFolder.children.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, Node> entry : ((Folder) other).children.entrySet()) {
            if (!newFolder.children.containsKey(entry.getKey()) && !newFolder.children.containsKey(entry.getKey() + ".cl")) {
                newFolder.children.put(entry.getKey(), entry.getValue());
            }
        }
        return newFolder;

    }

    public Folder clone() {
        Folder f = new Folder();
        for (Map.Entry<String, Node> entry : this.children.entrySet()) {
            f.add(entry.getKey(), entry.getValue());
        }

        return f;
    }
}