package fr.uca.jgit.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Commit implements JGitObject {
    private List<Commit> parents;
    private Folder state;

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
    public void store() { // How to get the message of the commit ?
        try {
            StringBuilder content = new StringBuilder();
            File myObj = new File(".git/logs/"+ this.hash());
            FileWriter myWriter = new FileWriter(".git/logs/"+ this.hash());

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            for(Commit c : parents){
                content.append(c.hash()).append(";");
            }
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

    /**
     * Create a new branch if not exist
     * @param branchName - name of the branch to create
     * @throws IOException
     */
    public static void createBranch(String branchName) throws IOException {
        Commit commit = new Commit();
        commit.store();
        String commitHash = commit.hash();
        createBranch(branchName, commitHash);
    }

    /**
     * Create a new branch if not exist
     * @param branchName - name of the branch to create
     * @param commitHash - commit hash of the branch
     * @throws IOException
     */
    public static void createBranch(String branchName, String commitHash) throws IOException {
        // Create a new file in .git/refs/heads with the given branch name
        File branchFile = new File(".git/refs/heads/" + branchName);
        if (branchFile.exists()) {
            System.out.println("fatal: A branch named '" + branchName + "' already exists.");
        } else {
            FileWriter myWriter = new FileWriter(branchFile);
            myWriter.write(commitHash);
            myWriter.close();

            System.out.println("Branch " + branchName + " created with head at commit " + commitHash);
        }
    }
}
