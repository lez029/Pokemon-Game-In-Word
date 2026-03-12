package Entity;

import Entity.Ability.Ability;
import Entity.Gender.Gender;
import Entity.Type.Type;

import java.util.ArrayList;
import java.util.Arrays;

public class Species {
    private Type type1;
    private Type type2;
    private String speciesName;
    private int id;
    private int baseHP;
    private int baseAttack;
    private int baseDefense;
    private int baseSA;
    private int baseSD;
    private int baseSpeed;
    private int baseTotal;
    private int[] evDefeat = new int[6];
    private int baseEXP;
    private EXPGroup expGroup;
    private ArrayList<Ability> abilityList;
    private double maleRatio;
    private boolean isGenderless;

    protected Species() {

    }

    public Type getType1() {
        return type1;
    }

    public Type getType2() {
        return type2;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public int getId() {
        return id;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getBaseSA() {
        return baseSA;
    }

    public int getBaseSD() {
        return baseSD;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public int getBaseEXP() {
        return baseEXP;
    }

    public int getBaseTotal() {
        this.baseTotal = baseHP
                + baseAttack
                + baseDefense
                + baseSA
                + baseSD
                + baseSpeed;
        return baseTotal;
    }

    public EXPGroup getExpGroup() {
        return expGroup;
    }

    public ArrayList<Ability> getAbilityList() {
        return new ArrayList<>(abilityList);
    }

    public int[] getEVDefeat() {
        return Arrays.copyOf(evDefeat, evDefeat.length);
    }
}
