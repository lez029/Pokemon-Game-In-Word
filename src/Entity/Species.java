package Entity;

import Entity.Ability.Ability;
import Entity.Gender.Gender;
import Entity.Type.Type;

import java.util.ArrayList;
import java.util.Arrays;

public class Species {
    public final Type type1;
    public final Type type2;
    public final String speciesName;
    public final int id;
    public final int baseHP;
    public final int baseAttack;
    public final int baseDefense;
    public final int baseSA;
    public final int baseSD;
    public final int baseSpeed;
    public final int baseTotal;
    public final int[] evDefeat;
    public final int baseEXP;
    public final EXPGroup expGroup;
    public final ArrayList<Ability> abilityList;
    public final ArrayList<Item> itemList;
    public final double maleRatio;
    public final boolean isGenderless;

    protected Species(Type type1,
                      Type type2,
                      String speciesName,
                      int id,
                      int baseHP,
                      int baseAttack,
                      int baseDefense,
                      int baseSA,
                      int baseSD,
                      int baseSpeed,
                      int baseTotal,
                      int[] evDefeat,
                      int baseEXP,
                      EXPGroup expGroup,
                      ArrayList<Ability> abilityList,
                      double maleRatio,
                      boolean isGenderless,
                      ArrayList<Item> itemList) {

        this.type1 = type1;
        this.type2 = type2;
        this.speciesName = speciesName;
        this.id = id;
        this.baseHP = baseHP;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseSA = baseSA;
        this.baseSD = baseSD;
        this.baseSpeed = baseSpeed;
        this.baseTotal = getBaseTotal();
        this.evDefeat = evDefeat;
        this.baseEXP = baseEXP;
        this.expGroup = expGroup;
        this.abilityList = abilityList;
        this.maleRatio = maleRatio;
        this.isGenderless = isGenderless;
        this.itemList = itemList;
    }

    public int getBaseTotal() {
        return baseHP
                + baseAttack
                + baseDefense
                + baseSA
                + baseSD
                + baseSpeed;
    }
}
