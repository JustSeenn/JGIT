package fr.uca.jgit.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;


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
            FileWriter myWriter = new FileWriter("./jgit/object/"+this.hash());

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
            File myObj = new File("./jgit/object/"+hash);
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

    /** Merges the given file with this file. **/
    @Override
    public Node merge(Node other) {
        TextFile otherFile = (TextFile) other;
        List<String> file1Lines = List.of(this.content.split("\n"));
        List<String> file2Lines = List.of(otherFile.content.split("\n"));
        StringBuilder output = new StringBuilder();

        int i = 0;
        int j = 0;

        while (i < file1Lines.size() && j < file2Lines.size()) {
            String line1 = file1Lines.get(i);
            String line2 = file2Lines.get(j);
            if (line1.equals(line2)) {
                output.append(line1).append("\n");

            } else {
                System.out.println("Conflict detected at the line " + (i + 1) + ": ");
                System.out.println("Version 1: " + line1);
                System.out.println("Version 2: " + line2);
                System.out.println("Choose if you want the version (1 or 2):");
                Scanner scanner = new Scanner(System.in);
                int choice = scanner.nextInt();
                if (choice == 1) {
                    output.append(line1).append("\n");

                } else {
                    output.append(line2).append("\n");
                }
            }
            i++;
            j++;
        }

        while (i < file1Lines.size()) {
            output.append(file1Lines.get(i)).append("\n");
            i++;
        }

        while (j < file2Lines.size()) {
            output.append(file2Lines.get(j)).append("\n");
            j++;
        }

        String outputStr = output.toString();
        TextFile mergedFile = new TextFile();
        mergedFile.content = outputStr;
        return mergedFile;
    }
}
