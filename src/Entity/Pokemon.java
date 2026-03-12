package Entity;

import Entity.Ability.Ability;
import Entity.Gender.Gender;
import Entity.Move.Move;
import Entity.Status.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Pokemon implements Cloneable{
    //Global Variables
    private final Species species;
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
    private ArrayList<Move> moveListNow = new ArrayList<>(4);
    private ArrayList<Move> moveListAvailable = new ArrayList<>();
    private Status status;
    private Gender gender;
    private Ability ability;
    private Player owner;

    // Constants
    private static final Random random = new Random();
    private static final int INDEX_HP = 0;
    private static final int INDEX_ATTACK = 1;
    private static final int INDEX_DEFENSE = 2;
    private static final int INDEX_SA = 3;
    private static final int INDEX_SD = 4;
    private static final int INDEX_SPEED = 5;

    protected Pokemon(Species species, int level) {
        this.species = species;
        this.nickname = species.getSpeciesName();
        initRandomAttributes();
        this.level = level;
        this.ev = new int[]{0, 0, 0, 0, 0, 0};
        setAllStats();
        //Add Gender Random
        generateAbility(species.getAbilityList());
    }

    protected Pokemon(Species species, int level, Player owner) {
        this.species = species;
        this.nickname = species.getSpeciesName();
        this.owner = owner;
        initRandomAttributes();
        this.level = level;
        this.ev = new int[]{0, 0, 0, 0, 0, 0};
        setAllStats();
        generateAbility(species.getAbilityList());
        if (owner instanceof RealPlayer) {
            ownerType = OwnerType.PLAYER;
            this.expNow = 0;
            setEXPNeed();
        }
        else
            ownerType = OwnerType.OTHER;
    }

    // Deep Copy the input pokemon to the output pokemon
    // after successfully catching or trading
    public Pokemon(Pokemon pokemon, Player owner) {
        Pokemon copy = pokemon.clone();
        copy.owner = owner;
        copy.ownerType = OwnerType.PLAYER;
        this.species = copy.species;
        this.nickname = copy.nickname;
        this.ownerType = copy.ownerType;
        this.level = copy.level;
        this.owner = copy.owner;
        this.nature = copy.nature;
        this.ev = copy.ev;
        this.iv = copy.iv;
        this.itemNow = copy.itemNow;
        this.moveListNow = copy.moveListNow;
        this.moveListAvailable = copy.moveListAvailable;
        this.status = copy.status;
        this.gender = copy.gender;
        this.expNow = copy.expNow;
        this.expNeed = copy.expNeed;
        this.maxHP = copy.maxHP;
        this.currentHP = copy.currentHP;
        this.attack = copy.attack;
        this.defense = copy.defense;
        this.SA = copy.SA;
        this.SD = copy.SD;
        this.speed = copy.speed;
        this.ability = copy.ability;
    }

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
            copy.status = this.status != null ? this.status.clone() : null;
            copy.itemNow = this.itemNow != null ? this.itemNow.clone() : null;
            copy.gender = this.gender != null ? this.gender.clone() : null;
            copy.ability = this.ability != null ? this.ability.clone() : null;
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

    private void initRandomAttributes() {
        for (int i = 0; i < iv.length; i++) iv[i] = random.nextInt(32);
        this.nature = Nature.values()[random.nextInt(Nature.values().length)];
    }

    public void generateAbility(ArrayList<Ability> list) {
        ability = list.get(random.nextInt(list.size()));
    }

    public int calculateMaxHP() {
        return (2 * species.getBaseHP()
                + iv[INDEX_HP]
                + ev[INDEX_HP] / 4)
                * level / 100 + level + 10;
    }

    public int calculateOtherStats(int index, int baseVal) {
        return (int)(
                ((double) (2 * baseVal
                        + iv[index] + ev[index] / 4)
                        * level / 100 + 5) * nature.getModifier()[index]);
    }

    public void setAllStats() {
        int oldMaxHP = maxHP;
        maxHP = calculateMaxHP();
        attack = calculateOtherStats(INDEX_ATTACK, species.getBaseAttack());
        defense = calculateOtherStats(INDEX_DEFENSE, species.getBaseDefense());
        SA = calculateOtherStats(INDEX_SA, species.getBaseSA());
        SD = calculateOtherStats(INDEX_SD, species.getBaseSD());
        speed = calculateOtherStats(INDEX_SPEED, species.getBaseSpeed());
        currentHP += maxHP - oldMaxHP;
    }

    public void setEXPNeed() {
        expNeed = species.getExpGroup().calculateEXPNeed(level);
    }

    public void addEXP(int expGet) {
        this.expNow += expGet;
        levelUP();
    }

    public void levelUP() {
        while (expNow >= expNeed && level < 100) {
            expNow -= expNeed;
            level++;
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

    public void takeDamage(int damage) {
        currentHP -= Math.min(damage, currentHP);
        if (isFainted()) {

        }
    }

    public boolean isFainted() {
        return currentHP <= 0;
    }
}