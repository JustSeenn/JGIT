package fr.uca.jgit;

import fr.uca.jgit.command.Add;
import fr.uca.jgit.command.Init;
import fr.uca.jgit.command.Merge;
import fr.uca.jgit.command.StateCommit;
import fr.uca.jgit.controller.RepositoryController;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("No command found");
            return;
        }
        String command = args[0];
        switch (command) {
            case "init" -> mainInit();
            case "commit" -> mainCommit(args[1]);
            case "merge" -> mainMerge(args[1]);
            case "add" -> mainAdd(args[1]);
            default -> System.out.println("Command not found");
        }
    }

    public static void mainInit() {
        Init init = new Init();
        try {
            init.execute();
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






}
