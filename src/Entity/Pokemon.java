package Entity;

import Data.SpeciesData;
import Entity.Ability.Ability;
import Entity.Gender.Gender;
import Entity.Move.Move;
import Entity.Status.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Pokemon implements Cloneable{
    //Global Variables
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

    public Pokemon(SpeciesData data, int level) {
        this.data = data;
        this.nickname = data.speciesName();
        initRandomAttributes();
        this.level = level;
        this.ev = new int[]{0, 0, 0, 0, 0, 0};
        setAllStats();
        //Add Gender Random
        generateAbility(data.abilityList());
    }

    public Pokemon(SpeciesData data, int level, Player owner) {
        this.data = data;
        this.nickname = data.speciesName();
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

    // Deep Copy the input pokemon to the output pokemon
    // after successfully catching or trading
    public Pokemon(Pokemon pokemon, Player owner) {
        Pokemon copy = pokemon.clone();
        copy.owner = owner;
        copy.ownerType = OwnerType.PLAYER;
        copyFrom(copy);
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
            copy.status = this.status;
            copy.itemNow = this.itemNow != null ? this.itemNow.clone() : null;
            copy.gender = this.gender != null ? this.gender.clone() : null;
            copy.ability = this.ability != null ? this.ability.clone() : null;
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

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
        this.gender = other.gender;
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

    private void initRandomAttributes() {
        for (int i = 0; i < iv.length; i++) iv[i] = random.nextInt(32);
        this.nature = Nature.values()[random.nextInt(Nature.values().length)];
    }

    public void generateAbility(ArrayList<Ability> list) {
        ability = list.get(random.nextInt(list.size()));
    }

    public int calculateMaxHP() {
        return (2 * data.baseHP()
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