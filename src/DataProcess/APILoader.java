package DataProcess;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APILoader {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> loadAPI(String url)
            throws IOException, InterruptedException {
        HttpRequest httpRequest =
                HttpRequest.newBuilder(URI.create(url)).GET().build();
        return client.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());
    }
}
