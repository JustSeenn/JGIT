package fr.uca.jgit;

import fr.uca.jgit.command.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("No command found");
            return;
        }
        String command = args[0];
        switch (command) {
            case "init" -> mainInit(args);
            case "commit" -> mainCommit(args[1]);
            case "merge" -> mainMerge(args[1]);
            case "add" -> mainAdd(args[1]);
            case "checkout" -> mainCheckout(args[1]);
            case "branch" -> mainBranch(args[1]);
            case "pull" -> mainPull();
            default -> System.out.println("Command not found");
        }
    }

    public static void mainInit(String... path) {
        Init init = new Init();
        try {
            init.execute(path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void mainAdd(String path) {
        Add add = new Add();
        add.execute(path);
    }

    public static void mainCommit(String message){
       StateCommit c = new StateCommit();
        c.execute(message);

    }

    public static void mainMerge(String commit) {
        Merge m = new Merge();
       m.execute(commit);
    }


    private static void mainCheckout(String hash) {
        Checkout checkout = new Checkout();
        checkout.execute(hash);
    }

    private static void mainBranch(String branchName){
        Branch branch = new Branch();
        branch.execute(branchName);
    }

    private static void mainPull(){
        Pull pull = new Pull();
        pull.execute();
    }
}
