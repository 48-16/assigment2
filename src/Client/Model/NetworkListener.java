package Client.Model;

import com.google.gson.JsonObject;

public interface NetworkListener {
    void onMessageReceived(JsonObject message);
}
