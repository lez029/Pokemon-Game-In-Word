package DataProcess;

import Entity.Ability.Ability;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class AbilityParser {
    public static ArrayList<Ability> abilitiesParser
            (HttpResponse<String> httpResponse)
            throws IOException, InterruptedException {
        ArrayList<Ability> abilityList = new ArrayList<Ability>();
        JSONObject root = new JSONObject(httpResponse.body());
        JSONArray abilities = root.getJSONArray("abilities");
        for (int i = 0; i < abilities.length(); i++) {
            JSONObject abilityObj = abilities.getJSONObject(i);
            abilityList.add(abilityParser(abilityObj));
        }
        return abilityList;
    }

    private static Ability abilityParser(JSONObject abilityObj)
            throws IOException, InterruptedException {
        boolean isHidden = abilityObj.getBoolean("is_hidden");
        int slot = abilityObj.getInt("slot");
        JSONObject ability = abilityObj.getJSONObject("ability");
        String name = ability.getString("name");
        String url = ability.getString("url");
        JSONObject root = new JSONObject(APILoader.loadAPI(url).body());
        String[] stringEffect = effectParser(root);
        String effect = stringEffect[0];
        String shortEffect = stringEffect[1];
        String description = descriptionParser(root);
        int id = idParser(root);
        return new Ability(name,
                shortEffect,
                description,
                effect,
                isHidden,
                slot,
                id);
    }

    private static String[] effectParser(JSONObject root)
            throws IOException, InterruptedException {
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

    private static String descriptionParser(JSONObject root)
        throws IOException, InterruptedException {
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

    private static int idParser(JSONObject root)
        throws IOException, InterruptedException {
        return root.getInt("id");
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {
        ArrayList<Ability> list = abilitiesParser(
                APILoader.loadAPI(
                        "https://pokeapi.co/api/v2/pokemon/24"));
        for (Ability a : list)
            System.out.print(a.name + "\n");
    }
}
