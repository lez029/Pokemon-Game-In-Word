package DataProcess;

import Entity.Ability.Ability;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public class AbilityParser {
    public static ArrayList<Ability> abilitiesParser(
            HttpResponse<String> httpResponse) {
        JSONObject root = new JSONObject(httpResponse.body());
        
    }
}
