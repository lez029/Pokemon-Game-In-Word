import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.*;

public class APITest {
    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon/25"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject root = new JSONObject(response.body());
        /**
        for (int i = 0; i < moves.length(); i++) {
            // Get this move
            JSONObject thisMove = moves.getJSONObject(i);
            String moveName = thisMove.getJSONObject("move").getString("name");
            JSONArray moveVersionDetail = thisMove.getJSONArray("version_group_details");
            for (int j = moveVersionDetail.length() - 1; j >= 0; j--) {
                JSONObject detail = moveVersionDetail.getJSONObject(j);
                JSONObject versionObj = detail.getJSONObject("version_group");
                String versionName = versionObj.getString("name");
                if (versionName.equals("sword-shield")) {
                    int levelLearned = detail.getInt("level_learned_at");
                    if (levelLearned > 0) {
                        System.out.printf("Move %d: %s\n", i, moveName);
                        System.out.printf("Level Learned: %d\n", levelLearned);
                    }
                    //else
                    //    System.out.print("Level Learned: Cannot Learned\n");
                    break;
                }
            }
        }
         */
    }
}
