package client;
import java.net.HttpURLConnection;
import java.net.URI;


public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public void clearDatabase() throws Exception {
        URI uri = new URI(serverUrl + "/db");

        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("DELETE");
        http.connect();
        int status = http.getResponseCode();
        if (status != 200) {
            throw new Exception("Failed to clear database, status code: " + status);
        }
    }
}