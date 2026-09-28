package Entity;

import Data.SpeciesData;
import Data.StatChange;
import Entity.Ability.Ability;
import Entity.Move.Move;
import Entity.Status.Status;

import java.util.*;

/**
 * Represents a Pokemon instance in the game
 * A Pokemon contains base data such as its pokemon species data
 * and other instance data like identity values and effort values
 *
 * @author Lele Zhang
 * @version 1.0
 */
public class Pokemon implements Cloneable{
    // TODO: Refactor Pokemon class to separate State, Combat Kit, and Context
    // Global Variables
    // ===Basic Data===
    public SpeciesData data;
    private String nickname;
    private int maxHP;
    private int attack;
    private int defense;
    private int SA;
    private int SD;
    private int speed;
    private Nature nature;
    private int[] iv = new int[6];

    // ===Progression Data===
    private int level;
    private int expNow;
    private int expNeed;
    private int[] ev = new int[6];
    // As some Pokemons may evolve based on their friendness
    private int friendness;
    private int lastProcessedLevel = 1;

    // ===Context===
    private OwnerType ownerType = OwnerType.WILD;
    private Player owner;


    // ===Combat Kit===
    private Item itemNow;
    private int currentHP;
    // Map of moves the Pokemon currently can use (max 4)
    private Map<Integer, Move> moveMapNow = new HashMap<>(4);
    // List of moves that are available for the Pokemon (no max or min)
    private Set<Move> moveListAvailable = new HashSet<Move>();
    private List<Move> moveListLearn;
    // Status now of the Pokemon
    private Status status;
    private Ability ability;
    private int slot;
    // TODO: Refactor statChanges
    private Map<String, StatChange> statChanges;

    // Constants
    private static final Random random = new Random();
    /*
     * Index for each stat out of a battle
     * 0 for Health Point
     * 1 for Attack
     * 2 for Defense
     * 3 for Special Attack
     * 4 for Special Defense
     * 5 for Speed
     */
    private static final int INDEX_HP = 0;
    private static final int INDEX_ATTACK = 1;
    private static final int INDEX_DEFENSE = 2;
    private static final int INDEX_SA = 3;
    private static final int INDEX_SD = 4;
    private static final int INDEX_SPEED = 5;

    // Default Constructor
    /**
     * Create a Pokemon with　given species data and level
     * (Mostly WILD Pokemon).
     *
     * @param data the SpeciesData of the Pokemon
     * @param level the initial level of the Pokemon
     */
    public Pokemon(SpeciesData data, int level) {
        this.data = data;
        this.nickname = data.speciesName();
        this.friendness = data.baseFriendness();
        initRandomAttributes();
        this.level = level;
        this.ev = new int[]{0, 0, 0, 0, 0, 0};
        setAllStats();
        //Add Gender Random
        generateAbility(data.abilityList());
    }

    /**
     * Create a Pokemon with given species data and level for a specific player.
     * For example, generate a new Pokemon for a NPC to battle with the player.
     * ALso, it can be used to create a new Pokemon for a new Player
     * in the game.
     *
     * @param data the SpeciesData of the Pokemon
     * @param level the level of the Pokemon
     * @param owner the owner of the Pokemon (NPC, Trainer, or Player)
     */
    public Pokemon(SpeciesData data, int level, Player owner) {
        this.data = data;
        this.nickname = data.speciesName();
        this.friendness = data.baseFriendness();
        this.owner = owner;
        initRandomAttributes();
        this.level = level;
        this.ev = new int[]{0, 0, 0, 0, 0, 0};
        setAllStats();
        generateAbility(data.abilityList());
        if (owner instanceof RealPlayer) {
            ownerType = OwnerType.PLAYER;
            this.expNow = 0;
            setEXPNeed();
        }
        else
            ownerType = OwnerType.OTHER;
    }

    /**
     * Deep copy the input pokemon to the output pokemon after successfully
     * catching or trading and create a new Pokemon with all data from original
     * Pokemon as input with the call of copyFrom(Pokemon other).
     *
     * @param pokemon the original Pokemon (Caught or Traded)
     * @param owner the owner of the Pokemon caught or traded
     */
    public Pokemon(Pokemon pokemon, Player owner) {
        Pokemon copy = pokemon.clone();
        this.friendness = copy.data.baseFriendness();
        copy.owner = owner;
        copy.ownerType = OwnerType.PLAYER;
        copyFrom(copy);
    }

    /**
     * Deep copy this Pokemon and assign all data to a new Pokemon instance.
     *
     * @return a new Pokemon instance copied deeply to avoid effects on the
     * original one.
     */
    @Override
    public Pokemon clone() {
        try {
            Pokemon copy = (Pokemon) super.clone();
            copy.ev = Arrays.copyOf(this.ev, this.ev.length);
            copy.iv = Arrays.copyOf(this.iv, this.iv.length);
            copy.moveMapNow = this.moveMapNow;
            copy.moveListAvailable = this.moveListAvailable;
            copy.status = this.status;
            copy.itemNow = this.itemNow != null ? this.itemNow.clone() : null;
            copy.ability = this.ability != null ? this.ability.clone() : null;
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

    /**
     * Shallow copy a Pokemon instance with all its reference variables and
     * primitive variables (as we will do deep copy first with the call
     * of clone()).
     *
     * @param other
     */
    private void copyFrom(Pokemon other) {
        this.data = other.data;
        this.nickname = other.nickname;
        this.ownerType = other.ownerType;
        this.level = other.level;
        this.owner = other.owner;
        this.nature = other.nature;
        this.ev = other.ev;
        this.iv = other.iv;
        this.itemNow = other.itemNow;
        this.moveMapNow = other.moveMapNow;
        this.moveListAvailable = other.moveListAvailable;
        this.status = other.status;
        this.expNow = other.expNow;
        this.expNeed = other.expNeed;
        this.maxHP = other.maxHP;
        this.currentHP = other.currentHP;
        this.attack = other.attack;
        this.defense = other.defense;
        this.SA = other.SA;
        this.SD = other.SD;
        this.speed = other.speed;
        this.ability = other.ability;
    }


    /**
     * Set iv[] to initial random value (0~31) for each stat.
     * Generate a random nature for the Pokemon instance and make it final.
     */
    private void initRandomAttributes() {
        for (int i = 0; i < iv.length; i++) iv[i] = random.nextInt(32);
        this.nature = Nature.values()[random.nextInt(Nature.values().length)];
    }

    /**
     * Generate the ability of the Pokemon instance from its speices available
     * ability list, only Nomral Abilities (ALways max 2 for a Pokemon).
     * If players want to get instances with Hidden Ability, they need to hatch.
     *
     * @param list an array list of all available abilities of the Pokemon
     */
    public void generateAbility(List<Ability> list) {
        int slot = random.nextInt(list.size()) + 1;
        this.slot = slot;
        this.ability = list.get(slot - 1);
    }

    /**
     * Calculate the max health points of the Pokemon based on its species base
     * stats, identity value, effort value, and level.
     * Formula: (2 * BaseHP + ivHP + evHP / 4) * level /100 + level + 10
     * (Ceil down)
     * Generation: III and after III
     *
     * @return the max health points calculated based on data above
     */
    public int calculateMaxHP() {
        return (2 * data.baseHP()
                + iv[INDEX_HP]
                + ev[INDEX_HP] / 4)
                * level / 100 + level + 10;
    }

    /**
     * Calculate other current stats of a Pokemon after its initialization,
     * levelUp, or evolve based on its species base stats, identity value,
     * effort value, and level
     * Formula: (((2 * BaseVal + iv[baseVal] + ev[baseVal] / 4) * level / 100
     * + 5) * natureModifier) (Ceil down)
     * Generation: III and after III
     *
     * @param index index of each stat (0=HP, 1=Attack, 2=Defense,
     *              3=Special Attack, 4=Special Defense, 5=Speed)
     * @param baseVal the number of each base stat got from the SpeciesData
     * @return integar calculation result of each stat
     */
    public int calculateOtherStats(int index, int baseVal) {
        return (int)(
                ((double) (2 * baseVal
                        + iv[index] + ev[index] / 4)
                        * level / 100 + 5) * nature.getModifier()[index]);
    }

    // Getter Methods
    public int getCurrentHP(){return currentHP;}
    public int getAttack(){return attack;}
    public int getDefense(){return defense;}
    public int getMaxHP(){return maxHP;}
    public int getSA(){return SA;}
    public int getSD(){return SD;}
    public int getLevel(){return level;}
    public int getSpeed(){return speed;}
    public Map<String, StatChange> getStatChanges(){return statChanges;}
    public Player getOwner() {
        return owner;
    }

    public Map<Integer, Move> getmoveMapNow() {
        return moveMapNow;
    }

    /**
     * Calculate every Statistic and assign it to data field
     * This happens when a pokemon levelUp or evolve()
     */
    public void setAllStats() {
        int oldMaxHP = maxHP;
        maxHP = calculateMaxHP();
        attack = calculateOtherStats(INDEX_ATTACK, data.baseAttack());
        defense = calculateOtherStats(INDEX_DEFENSE, data.baseDefense());
        SA = calculateOtherStats(INDEX_SA, data.baseSA());
        SD = calculateOtherStats(INDEX_SD, data.baseSD());
        speed = calculateOtherStats(INDEX_SPEED, data.baseSpeed());
        currentHP += maxHP - oldMaxHP;
    }

    /**
     * Calculate experience needed for current level and set it
     */
    public void setEXPNeed() {
        expNeed = data.expGroup().calculateEXPNeed(level);
    }

    /**
     * Add experience to expNow to make sure the experience increase
     * @param expGet int experience points got and needed to be added to expNow
     */
    public void addEXP(int expGet) {
        this.expNow += expGet;
        levelUP();
    }

    // TODO: Implement levelUP method (call evolve() if isEvolve is true)

    /**
     * Increase level of the pokemon, if it evolve,
     * renew its species data first, and finally, re-calculate its stats.
     * Make sure that level will not exceed lv.100, which is maximum level;
     */
    public void levelUP() {
        while (expNow >= expNeed && level < 100) {
            expNow -= expNeed;
            level++;
            if (data.canEvolve() && level == data.evolveLevel()) {
                evolve();
            }
            setEXPNeed();
            setAllStats();
        }
        // Ensure expNow never overflows
        if (level == 100) {
            expNow = Math.min(expNow, expNeed);
            System.out.printf("%s have reached highest level: %d\n",
                    nickname,
                    level);
        }
    }

    /**
     * Reset the nickname of pokemon (Default Name: Species Name)
     * @param newName String new name for the pokemon
     * @return
     */
    public boolean rename(String newName) {
        if (newName != null && !newName.isBlank()) {
            nickname = newName;
            return true;
        }
        else
            return false;
    }

    // TODO: Implement the attack logic in battle system, because attack()
    //  will be evoked only when a pokemon is in a battle
    /**
     * Attack the target pokemon with selected move
     * @param target Pokemon the target pokemon
     * @param move Move the selected move
     * @return damage calculated based on game rules if attack isHit true;
     * or effect
     * otherwise, -1 if missed;
     */
    public int attack(Pokemon target, Move move) {
        return 0;
    }

    // TODO: Implement the takeDamage logic in battle system
    public void takeDamage(int damage) {
        currentHP -= Math.min(damage, currentHP);
        if (isFainted()) {

        }
    }

    /**
     * Check if the pokemon is fainted
     * @return true if the pokemon is fainted; false otherwise
     */
    public boolean isFainted() {
        return currentHP <= 0;
    }

    // TODO: Implement evolve() method

    /**
     * Evolve the pokemon to its evolveToSpecies, renew its SpeciesData,
     * assign corresponding ability according to its original abilit slot,
     * and renew its move pool
     */
    private void evolve() {
        Data.SpeciesData evolveToData = data.evolveToData();
        data = evolveToData;
        // Inherit the ability with the same slot
        if (slot != 3)
            // Slot - 1 means to its index in a list
            ability = evolveToData.abilityList().get(slot - 1);
        else
            ability = evolveToData.hiddenAbility();
        lastProcessedLevel = 1;
        for (;lastProcessedLevel < level + 1; lastProcessedLevel++) {
            // TODO: Implement MoveList and LearnSet
        }
    }
    // TODO: Add public final boolean isEvolve data field to SpeciesData class
}