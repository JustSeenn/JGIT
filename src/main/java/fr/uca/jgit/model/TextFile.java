package fr.uca.jgit.model;

import java.io.*;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

import java.nio.file.Path;


public class TextFile implements Node {
    private String content;

    /**
     * For dev purpose
     **/
    public TextFile(String content) {
        this.content = content;
    }

    public TextFile() {
        this.content = "";
    }

    public String getContent() {
        return this.content;
    }

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

    /**
     * Stores the corresponding object in .git directory (to file .git/object/[hash]).
     **/
    @Override
    public void store() {
        try {
            Path filePath = WorkingDirectory.getInstance().getPath(".jgit", "object", this.hash());
            FileWriter myWriter = new FileWriter(filePath.toString());

            myWriter.write(this.content);
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Loads the text file corresponding to the given hash (from file .git/object/[hash]).
     **/
    public static TextFile loadFile(String hash) {
        StringBuilder content = new StringBuilder();
        try {
            Path filePath = WorkingDirectory.getInstance().getPath(".jgit", "object", hash);

            File myObj = new File(filePath.toString());
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

    /**
     * Restores the file node at the given path.
     **/
    @Override
    public void restore(String path) {
        try {
            File myObj = new File(path);
            FileWriter myWriter = new FileWriter(path);

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }
            myWriter.write(this.content);
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public String merge(List<String>  rightLines, List<String>  originalLines) {

        List<String> leftLines = List.of(this.getContent().split("\n"));


        List<String> mergedLines = new ArrayList<>();
        for (int i = 0; i < originalLines.size(); i++) {
            String originalLine = originalLines.get(i);
            String leftLine = getLineWithClosestMatch(originalLine, leftLines);
            String rightLine = getLineWithClosestMatch(originalLine, rightLines);

            if (leftLine.equals(rightLine)) {
                mergedLines.add(leftLine);
            } else if (originalLine.equals(leftLine)) {
                mergedLines.add(rightLine);
            } else if (originalLine.equals(rightLine)) {
                mergedLines.add(leftLine);
            } else {
                mergedLines.add("<<<<<<<<<");
                mergedLines.add(leftLine);
                mergedLines.add("===========");
                mergedLines.add(rightLine);
                mergedLines.add(">>>>>>>>>");
            }
        }

        return String.join("\n", mergedLines) + "\n";
    }

    private static String getLineWithClosestMatch(String target, List<String> lines) {
        int minDistance = Integer.MAX_VALUE;
        String closestLine = null;

        for (String line : lines) {
            int distance = getLevenshteinDistance(target, line);
            if (distance < minDistance) {
                minDistance = distance;
                closestLine = line;
            }
        }

        return closestLine;
    }

    private static int getLevenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 1; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[m][n];
    }



    public TextFile clone() {
        TextFile clone = new TextFile();
        clone.content = new String(this.content);
        return clone;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
