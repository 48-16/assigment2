package server;

import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;
    private static Logger logger = Logger.getInstance();
    private static final Gson gson = new Gson();

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            logger.log("Error setting up client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                logger.log("Received from client: " + inputLine);
                try {
                    JsonObject jsonRequest = JsonParser.parseString(inputLine).getAsJsonObject();
                    handleRequest(jsonRequest);
                } catch (JsonSyntaxException e) {
                    sendErrorResponse("Invalid JSON format");
                }
            }
        } catch (IOException e) {
            logger.log("Client handler error: " + e.getMessage());
        } finally {
            try {
                server.removeClient(clientSocket);
                clientSocket.close();
            } catch (IOException e) {
                logger.log("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleRequest(JsonObject jsonRequest) {
        String action = jsonRequest.get("action").getAsString();

        switch (action) {
            case "getAllVinyls":
                getAllVinyls();
                break;
            case "reserveVinyl":
                reserveVinyl(jsonRequest);
                break;
            case "borrowVinyl":
                borrowVinyl(jsonRequest);
                break;
            case "returnVinyl":
                returnVinyl(jsonRequest);
                break;
            case "removeVinyl":
                removeVinyl(jsonRequest);
                break;
            default:
                sendErrorResponse("Unknown action: " + action);
                break;
        }
    }

    private void getAllVinyls() {
        JsonObject response = new JsonObject();
        response.addProperty("type", "response");
        response.addProperty("action", "getAllVinyls");
        response.add("vinyls", gson.toJsonTree(server.getVinylRepository().getAllVinylsAsJSON()));

        out.println(gson.toJson(response));
    }

    private void reserveVinyl(JsonObject request) {
        int vinylId = request.get("vinylId").getAsInt();
        String clientId = request.get("clientId").getAsString();

        boolean success = server.getVinylRepository().reserveVinyl(vinylId, clientId);

        JsonObject response = new JsonObject();
        response.addProperty("type", "response");
        response.addProperty("action", "reserveVinyl");
        response.addProperty("success", success);
        response.addProperty("message", success ? "Vinyl successfully reserved" : "Failed to reserve vinyl - may be unavailable");

        out.println(gson.toJson(response));
    }

    private void borrowVinyl(JsonObject request) {
        int vinylId = request.get("vinylId").getAsInt();
        String clientId = request.get("clientId").getAsString();

        boolean success = server.getVinylRepository().borrowVinyl(vinylId, clientId);

        JsonObject response = new JsonObject();
        response.addProperty("type", "response");
        response.addProperty("action", "borrowVinyl");
        response.addProperty("success", success);
        response.addProperty("message", success ? "Vinyl successfully borrowed" : "Failed to borrow vinyl - may be unavailable");

        out.println(gson.toJson(response));
    }

    private void returnVinyl(JsonObject request) {
        int vinylId = request.get("vinylId").getAsInt();
        boolean success = server.getVinylRepository().returnVinyl(vinylId);

        JsonObject response = new JsonObject();
        response.addProperty("type", "response");
        response.addProperty("action", "returnVinyl");
        response.addProperty("success", success);
        response.addProperty("message", success ? "Vinyl successfully returned" : "Failed to return vinyl - may not be borrowed");

        out.println(gson.toJson(response));
    }

    private void removeVinyl(JsonObject request) {
        int vinylId = request.get("vinylId").getAsInt();
        boolean success = server.getVinylRepository().removeVinyl(vinylId);

        JsonObject response = new JsonObject();
        response.addProperty("type", "response");
        response.addProperty("action", "removeVinyl");
        response.addProperty("success", success);
        response.addProperty("message", success ? "Vinyl successfully removed" : "Failed to remove vinyl - may not exist");

        out.println(gson.toJson(response));
    }

    private void sendErrorResponse(String errorMessage) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "error");
        response.addProperty("message", errorMessage);

        out.println(gson.toJson(response));
    }
}
