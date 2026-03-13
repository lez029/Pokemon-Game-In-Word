package DataProcess;

import Entity.Type.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class TypeParser {
    public static ArrayList<Type> typesParser
            (HttpResponse<String> httpResponse) {
        ArrayList<Type> typeList = new ArrayList<Type>();
        JSONObject root = new JSONObject(httpResponse.body());
        JSONArray typesArray = root.getJSONArray("types");
        for (int i = 0; i < typesArray.length(); i++) {
            typeList.add(typeParser(typesArray.getJSONObject(i)));
        }
        return typeList;
    }

    public static Type typeParser(JSONObject typeObj) {
        String type = typeObj.getJSONObject("type").getString("name");
        try {
            return Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void main(String[] args)
            throws InterruptedException, IOException {
        ArrayList<Type> typeList = typesParser(APILoader.loadAPI
                ("https://pokeapi.co/api/v2/pokemon/1"));
        for (Type type : typeList)
            System.out.print(type.toString() + "\n");
    }
}
