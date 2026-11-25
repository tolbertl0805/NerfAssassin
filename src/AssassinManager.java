import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class AssassinManager {
    private static final Scanner scan = new Scanner(System.in);
    private AssassinLink killRingHead;
    private AssassinLink killRingTail;
    private AssassinLink graveyardHead = new AssassinLink("");
    private AssassinLink graveyardTail = graveyardHead;
    private final String saveFile;
    private boolean saved;
    /**
     * Constructs an AssassinManager object containing the names in the order
     * in which they appear.
     * @param names a list of the names of this game's participants.
     * Precondition: there are no empty Strings in names.
     */
    public AssassinManager(ArrayList<String> names, String saveName) {
        int random = (int)(Math.random()*names.size());
        killRingHead = new AssassinLink(names.get(random));
        AssassinLink curr = killRingHead;
        while (names.size()>1){
            names.remove(random);
            random = (int)(Math.random()*names.size());
            AssassinLink next = new AssassinLink(names.get(random));
            curr.next(next);
            curr=next;
        }
        killRingTail=curr;
        saveFile=saveName;
        saved=false;
    }

    public AssassinManager(String prevSave){
        System.out.println("\nLoading existing game");
        Scanner ringScan = new Scanner(prevSave+"/Kill Ring");
        killRingHead = new AssassinLink(ringScan.nextLine());
        AssassinLink curr = killRingHead;
        while (ringScan.hasNext()){
            curr.next(new AssassinLink(ringScan.nextLine()));
            curr=curr.getNext();
        }
        killRingTail=curr;
        if (new File(prevSave+"/Graveyard").exists()) {
            Scanner graveScan = new Scanner(prevSave + "/Graveyard");
            graveyardHead = new AssassinLink(graveScan.nextLine());
            curr = graveyardHead;
            curr.setKiller(graveScan.nextLine());
            while (graveScan.hasNext()){
                curr.next(new AssassinLink(graveScan.nextLine()));
                curr=curr.getNext();
                curr.setKiller(graveScan.nextLine());
            }
            graveyardTail = curr;
        }
        saveFile = prevSave;
        saved=false;
    }

    public boolean activeGameMenu() throws FileNotFoundException {
        System.out.println("\n** Active game menu **\n(A)dd new elimination\n(P)rint game status\n(I)ndividual status\n(S)ave the game\n(Q)uit Assassin Manager");
        char action = scan.nextLine().toUpperCase().charAt(0);
        if (action=='P'){
            System.out.print(this.toStringKillRing()+"\n"+this.toStringGraveyardReverse());
        }
        else if (action=='A'){
            System.out.print("Next player eliminated? ");
            String victim = scan.nextLine();
            if (!kill(victim)){
                System.out.println("\n*** Invalid player name! ***");
            }
            saved=false;
        }
        else if (action=='I'){
            System.out.print("What player's status would you like to see? ");
            String name = scan.nextLine();
            if (this.isIn(name, true)){
                AssassinLink player = this.find(name, true);
                int killCount = player.getKills();
                if (killCount==1){
                    System.out.println(name+" is still in the game, has 1 kill, and is stalking "+player.getNext().getName()+".");
                }
                else {
                    System.out.println(name+" is still in the game, has "+player.getKills()+" kills, and is stalking "+player.getNext().getName()+".");
                }
            } else if (this.isIn(name, false)){
                AssassinLink player = this.find(name, false);
                int killCount = player.getKills();
                if (killCount==1){
                    System.out.println(name+" was killed by "+player.getKiller()+" and had 1 kill.");
                }
                else {
                    System.out.println(name+" was killed by "+player.getKiller()+" and had "+killCount+" kills.");
                }
            } else {
                System.out.println("There is no player named "+name+".");
            }
        }
        else if (action=='S'){
            this.save();
            saved=true;
        }
        else if (action=='Q'){
            if (!(new File(saveFile).exists())||!saved){
                System.out.print("\n**** You have modified the game since the last save.  ***\nSave your changes? (y/n): ");
                char response = scan.nextLine().toLowerCase().charAt(0);
                if (response=='y'){
                    this.save();
                }
            }
            return false;
        }
        else {
            System.out.println("\n*** Invalid action! ***");
        }
        return true;
    }

    /**
     * Creates a String representation of the current state of the game.
     * @return a String representation of the current kill ring begin with
     * "Current kill ring (<number> player(s) remaining)".
     * Then, each person in the ring should generate a line of text stating
     * "<this person> is stalking <this person's target>."
     * If there is only one person in the kill ring, it should report that the
     * person is stalking themselves.
     */
    public String toStringKillRing() {
        StringBuilder retStr = new StringBuilder("Current kill ring ("+this.left()+" players remaining):\n");
        AssassinLink curr = killRingHead;
        while (curr!=killRingTail){
            retStr.append(curr.getName()).append(" is stalking ").append(curr.getNext().getName()).append(".\n");
            curr=curr.getNext();
        }
        return retStr+curr.getName()+" is stalking "+killRingHead.getName()+".\n";
    }

    /**
     * Creates a String including the players currently in the graveyard,
     * one per line in the order that they appear in the list.
     * @return a String consisting of the names of the people in the graveyard,
     * one per line.  The String should begin with
     * "Current graveyard (<number> player(s) eliminated)".  Then, each person
     * in the graveyard should generate a line of text stating
     * "<this person> was killed by <this person's killer>".  The items should
     * be printed in reverse order, with the most recently eliminated first.
     */
    public String toStringGraveyard() {
        StringBuilder retStr = new StringBuilder();
        if (this.eliminated()==0){
            retStr.append("Empty\n");
            return retStr.toString();
        }
        AssassinLink curr = graveyardHead;
        while (curr!=graveyardTail){
            retStr.append(curr.getName()).append(" was killed by ").append(curr.getKiller()).append(".\n");
            curr=curr.getNext();
        }
        return retStr+curr.getName()+" was killed by "+curr.getKiller();
    }

    /**
     * Creates a String including the players currently in the graveyard,
     * one per line in the reverse order that they appear in the list.
     * @return a String including the names of the people in the graveyard, one
     * per line.  Each person in the graveyard should generate a line of text
     * stating "<this person> was killed by <this person's killer>".  The items
     * should be printed in order in which they were eliminated.
     */
    public String toStringGraveyardReverse() {
        StringBuilder retStr = new StringBuilder("Current graveyard ("+this.eliminated()+" players eliminated):\n");
        if (this.eliminated()==0){
            retStr.append("Empty\n");
            return retStr.toString();
        }
        AssassinLink curr = graveyardHead;
        while (curr!=graveyardTail){
            curr=curr.getNext();
            retStr.insert(0, curr.getName() + " was killed by " + curr.getKiller() + ".\n");
        }
        return retStr.toString();
    }

    /**
     * Determine if the game is over.
     * @return true if the game is over
     */
    public boolean gameOver() {
        return killRingHead==killRingTail;
    }

    /**
     * 	Determine the name of the game winner.
     * @return the winner's name or null if the game is incomplete
     */
    public String winner() {
        if (this.gameOver()){
            return killRingHead.getName();
        }
        return null;
    }

    /**
     * 	Moves the person specified from the kill ring to the head of the
     * 	graveyard.  This method should not create any new AssassinLinks,
     * 	although it will require some shifting of data.
     * @param name name of the person being killed.
     * @return true if it is able to remove the player. If the person does not
     * exist in the kill ring, it should return false.
     */
    public boolean kill(String name) {
        if (name.equals(killRingHead.getName())){
            return kill(killRingTail);
        }
        AssassinLink curr = killRingHead;
        while (!curr.getNext().getName().equals(name)){
            curr=curr.getNext();
            if (curr==killRingTail){
                return false;
            }
        }
        return kill(curr);
    }

    /**
     * Performs the same task as the other kill function, but now
     * requiring the ref to the AssassinLink that performed the kill.
     * @param killer assassin that successfully killed their target.
     * @return Always true, because this function is only referenced
     * whenever killer can perform a kill.
     */
    private boolean kill(AssassinLink killer){
        killer.kill();
        AssassinLink killed;
        if (killer==killRingTail){
            killed = killRingHead;
            killRingHead=killed.getNext();
        }
        else {
            killed = killer.getNext();
        }
        killed.setKiller(killer.getName());
        if (graveyardHead.getName().isEmpty())
        {
            graveyardHead=killed;
        }
        else {
            graveyardTail.next(killed);
        }
        graveyardTail=killed;
        if (killed==killRingTail){
            killRingTail=killer;
            killer.next(killRingHead);
            return true;
        }
        killer.next(killed.getNext());
        return true;
    }

    /**
     * Returns the number of AssassinLinks in the kill ring.
     * @return The number of AssassinLinks in the kill ring.
     * @throws NullPointerException Uses the killRingHead and
     * killRingTail variables initialized upon construction.
     */
    public int left() throws NullPointerException{
        int i=1;
        AssassinLink curr=killRingHead;
        while (curr!=killRingTail){
            i++;
            curr=curr.getNext();
        }
        return i;
    }

    public int eliminated() {
        if (graveyardHead.getName().isEmpty()){
            return 0;
        }
        int i=1;
        AssassinLink curr=graveyardHead;
        while (curr!=graveyardTail){
            i++;
            curr=curr.getNext();
        }
        return i;
    }

    public void save() throws FileNotFoundException {
        if (!(new File(saveFile+"/State").exists())){
            new File(saveFile+"/State");
        }
        if (!(new File(saveFile+"/Kill Ring").exists())){
            new File(saveFile+"/Kill Ring");
        }
        if (!(new File(saveFile+"/Graveyard").exists())){
            new File(saveFile+"/Graveyard");
        }
        PrintWriter stateWriter = new PrintWriter(saveFile+"/State");
        stateWriter.println("active game");
        stateWriter.close();
        PrintWriter ringWriter = new PrintWriter(saveFile+"/Kill Ring");
        AssassinLink curr = killRingHead;
        while (curr!=killRingTail){
            ringWriter.println(curr.getName());
            curr=curr.getNext();
        }
        ringWriter.println(curr.getName());
        ringWriter.close();
        if (new File(saveFile+"/Graveyard").exists()) {
            PrintWriter graveWriter = new PrintWriter(saveFile + "/Graveyard");
            curr = graveyardHead;
            while (curr != graveyardTail) {
                graveWriter.println(curr.getName() + "\n" + curr.getKiller());
                curr = curr.getNext();
            }
            graveWriter.println(curr.getName()+"\n"+curr.getKiller());
            graveWriter.close();
        }
        System.out.println("\nSaved game successfully in \'"+saveFile+"\'.");
    }

    public AssassinLink find(String name, boolean killRing){
        if (killRing){
            if (killRingHead.getName().equals(name)){
                return killRingHead;
            }
            AssassinLink curr = killRingHead;
            while (curr!=killRingTail){
                if (curr.getNext().getName().equals(name)){
                    return curr.getNext();
                }
                curr=curr.getNext();
            }
            return null;
        }
        if (graveyardHead.getName().equals(name)){
            return graveyardHead;
        }
        AssassinLink curr = killRingHead;
        while (curr!=killRingTail){
            if (curr.getNext().getName().equals(name)){
                return curr.getNext();
            }
            curr=curr.getNext();
        }
        return null;
    }

    public boolean isIn(String name, boolean killRing){
        if (killRing){
            if (killRingHead.getName().equals(name)){
                return true;
            }
            AssassinLink curr = killRingHead;
            while (curr!=killRingTail) {
                if (curr.getNext().getName().equals(name)){
                    return true;
                }
                curr=curr.getNext();
            }
            return false;
        }
        if (graveyardHead.getName().equals(name)){
            return true;
        }
        AssassinLink curr = graveyardHead;
        while (curr!=graveyardTail){
            if (curr.getNext().getName().equals(name)){
                return true;
            }
            curr=curr.getNext();
        }
        return false;
    }

    public String winnerKills(){
        String retStr = killRingHead.getKills()+" kill";
        if (killRingHead.getKills()!=1){
            retStr+='s';
        }
        return retStr;
    }
}