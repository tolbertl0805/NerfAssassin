import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner scan = new Scanner(System.in);
    static boolean saved = false;

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Nerf Assassin Manager.\n");
        AssassinManager game = askForSaveName();
        if (game!=null) {
            while (game.activeGameMenu()) {
                if (game.gameOver()){
                    System.out.println("\n*** Game over ***\n"+game.winner() + " wins the game with "+game.winnerKills()+".\n\nThe final graveyard, in order of elimination:\n" + game.toStringGraveyard() + "\n\nThank you for using Assassin Manager.");
                }
            }

        }
    }

    public static AssassinManager askForSaveName() throws Exception {
        System.out.print("Enter the current game's name.\nUse a new name if you are starting a new game.\nGame Name: ");
        String saveName = scan.nextLine();
        if (new File(saveName).exists()){
            return load(saveName);
        }
        System.out.println("\nStarting a new game of Assassin.");
        return preGameMenu(new ArrayList<String>(), saveName);
    }

    public static AssassinManager load(String saveName) throws Exception {
        Scanner state = new Scanner(new File(saveName+"/State"));
        if (state.nextLine().equals("active game")){
            return new AssassinManager(saveName);
        }
        System.out.println("\nLoading existing game.");
        return preGameMenu(readNames(saveName), saveName);
    }

    public static ArrayList<String> readNames(String saveName){
        ArrayList<String> names = new ArrayList<String>();
        Scanner nameScan = new Scanner(saveName+"/Names");
        while (nameScan.hasNextLine()){
            String name = nameScan.nextLine();
            names.add(name);
        }
        return names;
    }

    public static AssassinManager preGameMenu(ArrayList<String> names, String saveName) throws Exception {
        System.out.println("\n** Pre-game menu **\n(R)egister new players\n(L)ist all players\n(B)egin the game\n(S)ave the game\n(Q)uit Assassin Manager");
        char action = scan.nextLine().toUpperCase().charAt(0);
        if (action=='R'){
            System.out.println("Enter the names of the players, one per line.\n(Enter the name DONE to quit.)");
            String name = scan.nextLine();
            while (!name.equals("DONE")){
                names.add(name);
                name = scan.nextLine();
            }
            saved=false;
            return preGameMenu(names, saveName);
        }
        if (action=='L'){
            for (String name : names) {
                System.out.println(name);
            }
            return preGameMenu(names, saveName);
        }
        if (action=='B'){
            System.out.print("*** Once you begin the game, you will no longer be able to register players.\nAre you sure you want to begin? (y/n) ");
            char response = scan.nextLine().toLowerCase().charAt(0);
            if (response=='y') {
                return new AssassinManager(names, saveName);
            }
            return preGameMenu(names, saveName);
        }
        if (action=='S'){
            save(saveName, names);
        }
        if (action=='Q'){
            if (saved) {
                System.out.print("\n**** You have modified the game since the last save. ***\nSave your changes? (y/n): ");
                char response = scan.nextLine().toLowerCase().charAt(0);
                System.out.println();
                if (response=='y'){
                    save(saveName, names);
                }
            }
            return null;
        }
        System.out.println("\nInvalid action!");
        return preGameMenu(names, saveName);
    }

    public static void save(String saveName, ArrayList<String> names) throws Exception {
        PrintWriter stateWriter;
        PrintWriter nameWriter;
        if (!(new File(saveName+"/State").exists())){
            new File(saveName+"/State", saveName+"/State");
            new File(saveName+"/Names", saveName+"/Names");
            stateWriter = new PrintWriter(saveName+"/State");
            nameWriter = new PrintWriter(saveName+"/Names");
        }
        else {
            stateWriter = new PrintWriter(saveName+"/State");
            nameWriter = new PrintWriter(saveName+"/Names");
        }
        stateWriter.println("pre-game");
        stateWriter.close();
        for (String name : names) {
            nameWriter.println(name);
        }
        nameWriter.close();
        saved=true;
    }
}