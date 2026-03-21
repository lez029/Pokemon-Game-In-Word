package Entity;

import Data.SpeciesData;
import Entity.Ability.Ability;
import Entity.Move.Move;
import Entity.Status.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Represents a Pokemon instance in the game
 * A Pokemon contains base data such as its pokemon species data
 * and other instance data like identity values and effort values
 *
 * @author Lele Zhang
 * @version 1.0
 */
public class Pokemon implements Cloneable{
    //Global Variables
    /**
     * Pokemon species data of this Pokemon
     */
    public SpeciesData data;
    private String nickname;
    private int maxHP;
    private int currentHP;
    private int attack;
    private int defense;
    private int SA;
    private int SD;
    private int speed;
    private int level;
    private int expNow;
    private int expNeed;
    private Nature nature;
    private OwnerType ownerType = OwnerType.WILD;
    private int[] ev = new int[6];
    private int[] iv = new int[6];
    private Item itemNow;
    /**
     * List of moves the Pokemon currently can use (max 4)
     */
    private ArrayList<Move> moveListNow = new ArrayList<>(4);
    /**
     * List of moves that are available for the Pokemon (no max or min)
     */
    private ArrayList<Move> moveListAvailable = new ArrayList<>();
    /**
     * Status now of the Pokemon
     */
    private Status status;
    private Ability ability;
    private Player owner;
    private int friendness;
    // As some Pokemons may evolve based on their friendness

    // Constants
    private static final Random random = new Random();
    /**
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
            copy.moveListNow = this.moveListNow.stream()
                    .map(Move::clone)
                    .collect(Collectors.toCollection(ArrayList::new));
            copy.moveListAvailable = this.moveListAvailable.stream()
                    .map(Move::clone)
                    .collect(Collectors.toCollection(ArrayList::new));
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
        this.moveListNow = other.moveListNow;
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
     * ability list (ALways max 3 for a Pokemon).
     *
     * @param list an array list of all available abilities of the Pokemon
     */
    public void generateAbility(ArrayList<Ability> list) {
        ability = list.get(random.nextInt(list.size()));
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

    /**
     *
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

    public void setEXPNeed() {
        expNeed = data.expGroup().calculateEXPNeed(level);
    }

    public void addEXP(int expGet) {
        this.expNow += expGet;
        levelUP();
    }

    // TODO: Implement levelUP method (call evolve() if isEvolve is true)
    public void levelUP() {
        while (expNow >= expNeed && level < 100) {
            expNow -= expNeed;
            level++;
            if (data.canEvolve()) {
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

    public boolean rename(String newName) {
        if (newName != null && !newName.isBlank()) {
            nickname = newName;
            return true;
        }
        else
            return false;
    }

    public boolean attack(Pokemon defender, Move move) {
        return false;
    }

    // TODO: Implement takeDamage(int damage) method
    public void takeDamage(int damage) {
        currentHP -= Math.min(damage, currentHP);
        if (isFainted()) {

        }
    }

    public boolean isFainted() {
        return currentHP <= 0;
    }

    // TODO: Implement evolve() method
    private void evolve() {
        this.data = data.evolveToData();
        int slot1 = this.ability.getAbilityData()
                .pokemonMap()
                .get(data.speciesName()).slot();
        for (int i = 0; i < data.evolveToData().abilityList().size(); i++) {
            int slot2 = this.data
                    .evolveToData()
                    .abilityList()
                    .get(i)
                    .getAbilityData()
                    .getSlot(data.evolveToData().speciesName());
        }
    }
    // TODO: Add public final boolean isEvolve data field to SpeciesData class
}