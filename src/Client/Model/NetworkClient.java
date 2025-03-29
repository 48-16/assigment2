package Client.Model;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NetworkClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String SERVER_ADDRESS;
    private final int SERVER_PORT;
    private String clientId;
    private List<NetworkListener> listeners = new ArrayList<>();
    private Gson gson;

    public NetworkClient(String serverAddress, int serverPort, String clientId) {
        this.SERVER_ADDRESS = serverAddress;
        this.SERVER_PORT = serverPort;
        this.clientId = clientId;
        this.gson = new Gson();
    }

    public boolean connect() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenForMessages).start();
            return true;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.err.println("Error reading from server: " + e.getMessage());
            }
        }
    }

    private void processMessage(String jsonMessage) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonMessage).getAsJsonObject();
            for (NetworkListener listener : listeners) {
                listener.onMessageReceived(jsonObject);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    public void addListener(NetworkListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NetworkListener listener) {
        listeners.remove(listener);
    }

    public void getAllVinyls() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "getAllVinyls");
        request.addProperty("clientId", clientId);
        sendRequest(request);
    }

    public void reserveVinyl(int vinylId) {
        JsonObject request = new JsonObject();
        request.addProperty("action", "reserveVinyl");
        request.addProperty("vinylId", vinylId);
        request.addProperty("clientId", clientId);
        sendRequest(request);
    }

    public void borrowVinyl(int vinylId) {
        JsonObject request = new JsonObject();
        request.addProperty("action", "borrowVinyl");
        request.addProperty("vinylId", vinylId);
        request.addProperty("clientId", clientId);
        sendRequest(request);
    }

    public void returnVinyl(int vinylId) {
        JsonObject request = new JsonObject();
        request.addProperty("action", "returnVinyl");
        request.addProperty("vinylId", vinylId);
        request.addProperty("clientId", clientId);
        sendRequest(request);
    }

    public void removeVinyl(int vinylId) {
        JsonObject request = new JsonObject();
        request.addProperty("action", "removeVinyl");
        request.addProperty("vinylId", vinylId);
        request.addProperty("clientId", clientId);
        sendRequest(request);
    }

    private void sendRequest(JsonObject request) {
        if (socket != null && !socket.isClosed()) {
            out.println(gson.toJson(request));
        } else {
            System.err.println("Cannot send request - not connected to server");
        }
    }
}