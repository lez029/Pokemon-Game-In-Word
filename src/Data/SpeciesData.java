package Data;

import Entity.Ability.Ability;
import Entity.EXPGroup;
import Entity.Item;
import Entity.Type.Type;

import java.util.ArrayList;

public record SpeciesData(int id,
                          String speciesName,
                          Type type1,
                          Type type2,
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
                          ArrayList<Item> itemList,
                          GenderData genderData,
                          int baseFriendness,
                          boolean canEvolve,
                          SpeciesData evolveToData) {
}
