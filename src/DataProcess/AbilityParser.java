package DataProcess;

import Data.AbilityData.AbilityData;
import Data.AbilityData.AbilityPokeInfo;
import Entity.Ability.Ability;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class AbilityParser {
    //Get all information of all possible abilities for a pokemon species
    public static ArrayList<Ability> abilitiesParser
            (String url)
            throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = APILoader.loadAPI(url);
        ArrayList<Ability> abilityList = new ArrayList<Ability>();
        JSONObject root = new JSONObject(httpResponse.body());
        JSONArray abilities = root.getJSONArray("abilities");
        for (int i = 0; i < abilities.length(); i++) {
            JSONObject abilityObj = abilities.getJSONObject(i);
            abilityList.add(abilityParser(abilityObj.
                    getJSONObject("ability")
                    .getString("url")));
        }
        return abilityList;
    }

    // Get all information of an ability
    public static Ability abilityParser(String url)
            throws IOException, InterruptedException {
        // Load API and assign the return to httpResponse
        HttpResponse<String> httpResponse = APILoader.loadAPI(url);
        // Declare a new JSONObject with httpResponse.body() as input
        JSONObject root = new JSONObject(httpResponse.body());
        int id = root.getInt("id");
        // Get String information of ability
        String abilityName = root.getString("name");
        String[] stringEffect = effectParser(root);
        String effect = stringEffect[0];
        String shortEffect = stringEffect[1];
        String description = descriptionParser(root);
        // Create a HashMap for is_hidden and slot
        HashMap<String, AbilityPokeInfo> pokeMap = new HashMap<>();
        // Declare pokemonArr to getJSONArray from root
        JSONArray pokemonArr = root.getJSONArray("pokemon");
        // Use for loop to put values into HashMaps
        for (int i = 0; i < pokemonArr.length(); i++) {
            JSONObject pokeObj = pokemonArr.getJSONObject(i);
            String pokeName = pokeObj.getJSONObject("pokemon").getString("name");
            AbilityPokeInfo pokeInfo =
                    new AbilityPokeInfo(pokeObj.getBoolean("is_hidden"),
                            pokeObj.getInt("slot"));
            pokeMap.put(pokeName, pokeInfo);
        }
        // Declare new AbilityData Object and return it

        AbilityData data = new AbilityData(id,
                abilityName,
                shortEffect,
                description,
                effect,
                pokeMap);
        return new Ability(data);
    }

    // Get String information of effects of an ability
    private static String[] effectParser(JSONObject root) {
        String[] stringsEffect = new String[2];
        JSONArray effectArr = root.getJSONArray("effect_entries");
        for(int i = 0; i < effectArr.length(); i++) {
            JSONObject effectObj = effectArr.getJSONObject(i);
            if (effectObj.getJSONObject("language")
                    .getString("name")
                    .equals("en")) {
                stringsEffect[0] = effectObj.getString("effect");
                stringsEffect[1] = effectObj.getString("short_effect");
                return stringsEffect;
            }
        }
        return stringsEffect;
    }

    // Get String information of description of an ability
    private static String descriptionParser(JSONObject root) {
        JSONArray flavorTextArr = root.getJSONArray("flavor_text_entries");
        for(int i = 0; i < flavorTextArr.length(); i++) {
            JSONObject flavorTextObj = flavorTextArr.getJSONObject(i);
            if (flavorTextObj
                    .getJSONObject("language")
                    .getString("name")
                    .equals("en")) {
                return flavorTextObj.getString("flavor_text");
            }
        }
        return "";
    }

    // Test the AbilityParser.class and all its methods
    public static void main(String[] args)
            throws IOException, InterruptedException {
        ArrayList<Ability> list =
                abilitiesParser("https://pokeapi.co/api/v2/pokemon/1");
        for (Ability a : list) {
            System.out.print(a.data.pokemonMap() + "\n");
        }
    }
}
