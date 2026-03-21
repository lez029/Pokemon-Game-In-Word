package Data.AbilityData;

import Entity.Pokemon;

import java.util.HashMap;
public record AbilityData(
        int id,
        String name,
        String shortEffect,
        String description,
        String effect,
        HashMap<String, AbilityPokeInfo> pokemonMap
) {
    public int getSlot(String speciesName) {
        return pokemonMap.get(speciesName).slot();
    }

    public boolean isHidden(String speciesName) {
        return pokemonMap.get(speciesName).isHidden();
    }
}

