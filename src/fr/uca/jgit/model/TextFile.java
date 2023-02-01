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
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("filename.txt");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOS = new GZIPOutputStream(baos);
            gzipOS.write(this.content.getBytes());
            gzipOS.close();
            baos.close();
            myWriter.write(baos.toString());
            myWriter.close();
        } catch (IOException e) {
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
            FileInputStream fis = new FileInputStream(".git/object"+ this.hash());
            GZIPInputStream gzipIS = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(path);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIS.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            gzipIS.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
