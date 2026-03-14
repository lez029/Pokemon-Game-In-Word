package Data.AbilityData;

import java.util.HashMap;
public record AbilityData(
        int id,
        String name,
        String shortEffect,
        String description,
        String effect,
        HashMap<String, AbilityPokeInfo> pokemonMap
) {}

