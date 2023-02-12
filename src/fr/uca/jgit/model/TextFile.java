package fr.uca.jgit.model;

import java.io.*;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TextFile implements Node {
    private String content;

    @Override
    public String hash() {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(this.content.getBytes());

            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b & 0xff));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

    /** Stores the corresponding object in .git directory (to file .git/object/[hash]). **/
    @Override
    public void store() {
        try {
            File myObj = new File(".git/object/"+ this.hash());
            FileWriter myWriter = new FileWriter(".git/object/"+ this.hash());

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myWriter.write(this.content); // compress it ?
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /** Loads the text file corresponding to the given hash (from file .git/object/[hash]). **/
    public static TextFile loadFile(String hash) {
        StringBuilder content = new StringBuilder();
        try {
            File myObj = new File(".git/object/"+hash);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                content.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        TextFile newTextFile = new TextFile();
        newTextFile.content = content.toString();
        return newTextFile;
    }

    /** Restores the file node at the given path. **/
    @Override
    public void restore(String path) {
        try {
            File myObj = new File(path);
            FileWriter myWriter = new FileWriter(path);

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myWriter.write(this.content);
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Change the current branch to a specified branch
     *
     * @param branchName - name of the branch to check out
     */
    public void changeBranch(String branchName) throws IOException {
        // Check if the branch exists
        File branchFile = new File(".git/refs/heads/" + branchName);
        if (!branchFile.exists()) {
            System.out.println("Branch " + branchName + " does not exist");
            return ;
        }

        // Get the hash of the head commit
        BufferedReader br = new BufferedReader(new FileReader(branchFile));
        String hash = br.readLine();
        br.close();

        // Load the commit with the given hash
        this.content = loadFile(hash).content;
        System.out.println("Switched to a new branch " + branchName);
    }
}
