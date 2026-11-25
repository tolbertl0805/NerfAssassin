/**
 * The AssassinLink class is used to store the information for one
 * player in the game of assassin.
 */
public class AssassinLink {
    /**
     * This person's name.
     */
    private String name;

    /**
     * The name of who killed this person,
     * <code>null</code> if still alive.
     */
    private String killer;

    /**
     * The next Link in the list.
     */
    private AssassinLink next;

    private int killCount;

    /**
     * Constructs a Link with the given name and a <code>null</code> link.
     *
     * @param name The name
     */
    public AssassinLink(String name) {
        this(name, null);
    }

    /**
     * Constructs a Link with the given name and next.
     *
     * @param name The name of the player.
     * @param next A reference to the next player Link.
     */
    public AssassinLink(String name, AssassinLink next) {
        this.name = name;
        this.killer = null;
        this.next = next;
        this.killCount=0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKiller() {
        return killer;
    }

    public void setKiller(String killer) {
        this.killer = killer;
    }

    public AssassinLink getNext() {
        return next;
    }

    /**
     * Getter for the Link's next field
     * @param next the value of the next field
     */
    public void next(AssassinLink next) {
        this.next = next;
    }

    public int getKills(){
        return killCount;
    }

    public void kill(){
        killCount++;
    }
}
