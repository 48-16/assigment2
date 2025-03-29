package Client.ViewModel;

import Client.Model.NetworkClient;
import Client.Model.NetworkListener;
import Client.Model.VinylModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

public class MainViewModel implements NetworkListener {
    private final NetworkClient networkClient;
    private final ObservableList<VinylModel> vinyls = FXCollections.observableArrayList();
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final StringProperty clientId = new SimpleStringProperty(UUID.randomUUID().toString().substring(0, 8));
    private final BooleanProperty connected = new SimpleBooleanProperty(false);
    private final Gson gson = new Gson();

    public MainViewModel(String serverAddress, int serverPort) {
        networkClient = new NetworkClient(serverAddress, serverPort, clientId.get());
        networkClient.addListener(this);
    }

    public boolean connect() {
        boolean result = networkClient.connect();
        connected.set(result);
        if (result) {
            refreshVinyls();
            statusMessage.set("Connected to server");
        } else {
            statusMessage.set("Failed to connect to server");
        }
        return result;
    }

    public void disconnect() {
        networkClient.disconnect();
        connected.set(false);
        statusMessage.set("Disconnected from server");
    }

    public void refreshVinyls() {
        networkClient.getAllVinyls();
    }

    public void reserveVinyl(VinylModel vinyl) {
        if (vinyl != null) {
            networkClient.reserveVinyl(vinyl.getId());
        }
    }

    public void borrowVinyl(VinylModel vinyl) {
        if (vinyl != null) {
            networkClient.borrowVinyl(vinyl.getId());
        }
    }

    public void returnVinyl(VinylModel vinyl) {
        if (vinyl != null) {
            networkClient.returnVinyl(vinyl.getId());
        }
    }

    public void removeVinyl(VinylModel vinyl) {
        if (vinyl != null) {
            networkClient.removeVinyl(vinyl.getId());
        }
    }

    @Override
    public void onMessageReceived(JsonObject message) {
        String type = message.get("type").getAsString();

        if ("response".equals(type)) {
            handleResponse(message);
        } else if ("notification".equals(type)) {
            handleNotification(message);
        } else if ("error".equals(type)) {
            Platform.runLater(() -> statusMessage.set("Error: " + message.get("message").getAsString()));
        }
    }

    private void handleResponse(JsonObject response) {
        String action = response.get("action").getAsString();

        if ("getAllVinyls".equals(action)) {
            JsonArray vinylsArray = response.getAsJsonArray("vinyls");
            Platform.runLater(() -> {
                vinyls.clear();
                vinylsArray.forEach(vinylObj -> vinyls.add(gson.fromJson(vinylObj, VinylModel.class)));
                statusMessage.set("Vinyl list updated");
            });
        } else {
            boolean success = response.get("success").getAsBoolean();
            String message = response.get("message").getAsString();

            Platform.runLater(() -> {
                if (success) {
                    statusMessage.set(message);
                    refreshVinyls();
                } else {
                    statusMessage.set("Operation failed: " + message);
                }
            });
        }
    }

    private void handleNotification(JsonObject notification) {
        String action = notification.get("action").getAsString();

        if ("vinylStateChanged".equals(action)) {
            int vinylId = notification.get("vinylId").getAsInt();
            String newState = notification.get("newState").getAsString();

            Platform.runLater(() -> {
                vinyls.stream()
                        .filter(vinyl -> vinyl.getId() == vinylId)
                        .findFirst()
                        .ifPresent(vinyl -> {
                            vinyl.setStatus(newState);
                            if (notification.has("clientId")) {
                                vinyl.setCurrentHolder(notification.get("clientId").getAsString());
                            }
                            statusMessage.set("Vinyl #" + vinylId + " is now " + newState);
                        });
            });
        } else if ("vinylRemoved".equals(action)) {
            int vinylId = notification.get("vinylId").getAsInt();

            Platform.runLater(() -> {
                vinyls.removeIf(vinyl -> vinyl.getId() == vinylId);
                statusMessage.set("Vinyl #" + vinylId + " has been removed");
            });
        }
    }

    // Getters for observable properties
    public ObservableList<VinylModel> getVinyls() {
        return vinyls;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public StringProperty clientIdProperty() {
        return clientId;
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }
}
