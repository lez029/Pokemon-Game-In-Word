package DataProcess;

import Data.MoveData;
import Data.StatChange;
import Entity.Move.DamageClass;
import Entity.Move.Move;
import Entity.Move.Stat;
import Entity.Move.Target;
import Entity.Type.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoveParser {

    // Get all Moves available to a pokemon species
    public static ArrayList<Move> movesParser(String url)
            throws InterruptedException, IOException {
        HttpResponse<String> httpResponse = APILoader.loadAPI(url);
        JSONObject root = new JSONObject(httpResponse.body());
        JSONArray movesArr = root.getJSONArray("moves");
        ArrayList<Move> moveAvailableList = new ArrayList<>();
        for (int i = 0; i < movesArr.length(); i ++) {
            JSONObject moveObj = movesArr.getJSONObject(i);
            moveAvailableList.add(moveParser(moveObj.getString("url")));
        }
        return moveAvailableList;
    }

    // Get information of a single Move
    public static Move moveParser(String url)
            throws InterruptedException, IOException {
        HttpResponse<String> httpResponse = APILoader.loadAPI(url);
        JSONObject root = new JSONObject(httpResponse.body());
        int accuracy = root.getInt("accuracy");
        DamageClass damageClass = DamageClass
                .valueOf(root
                        .getJSONObject("damage_class")
                        .getString("name")
                        .toUpperCase());
        int statusChance = root.optInt("effect_chance");
        String effect = root
                .getJSONArray("effect_entries")
                .getJSONObject(1)
                .getString("effect");
        String shortEffect = root
                .getJSONArray("effect_entries")
                .getJSONObject(1)
                .getString("short_effect");
        String flavorText = "";
        JSONArray flavorTextArr = root.getJSONArray("flavor_text_entries");
        for (int i = 0; i < flavorTextArr.length(); i++) {
            JSONObject flavorTextObj = flavorTextArr.getJSONObject(i);
            if (flavorTextObj
                    .getJSONObject("language")
                    .getString("name")
                    .equals("en")) {
                if (flavorTextObj
                        .getJSONObject("version_group")
                        .getString("name")
                        .equals("sword-shield"))
                    flavorText = flavorTextObj
                                .getString("flavor_text");
            }
        }
        int id = root.getInt("id");
        String name = root.getString("name");
        int power = root.optInt("power");
        int pp = root.getInt("pp");
        int priority = root.getInt("priority");
        JSONArray statChanges = root.getJSONArray("stat_changes");
        ArrayList<StatChange> statChangeArrayList = new ArrayList<StatChange>();
        for (int i = 0; i < statChanges.length(); i++) {
            JSONObject statChangeObj = statChanges.getJSONObject(i);
            int change = statChangeObj.getInt("change");
            Stat statName = Stat.valueOf(statChangeObj
                    .getJSONObject("stat")
                    .getString("name")
                    .toUpperCase()
                    .replace("-","_"));
            statChangeArrayList.add(new StatChange(statName, change));
        }
        Target target = Target.valueOf(root
                .getJSONObject("target")
                .getString("name").replace("-","_")
                .toUpperCase());
        Type type = Type.valueOf(root
                .getJSONObject("type")
                .getString("name")
                .toUpperCase());
        MoveData data = new MoveData(
            accuracy,
            damageClass,
            statusChance,
            effect,
            shortEffect,
            flavorText,
            id,
            name,
            power,
            pp,
            priority,
            statChangeArrayList,
            target,
            type);
        return new Move(data);
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {
        System.out.print(moveParser("https://pokeapi.co/api/v2/move/1").data.flavorText());
    }
}
