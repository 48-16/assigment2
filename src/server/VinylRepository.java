package server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class VinylRepository {
    private Map<Integer, Vinyl> vinyls;
    private List<VinylObserver> observers;
    private Gson gson;

    public VinylRepository() {
        vinyls = new ConcurrentHashMap<>();
        observers = new ArrayList<>();
        gson = new Gson();
    }

    public void addVinyl(Vinyl vinyl) {
        vinyls.put(vinyl.getId(), vinyl);
    }

    public boolean removeVinyl(int id) {
        if (vinyls.containsKey(id)) {
            vinyls.remove(id);
            return true;
        }
        return false;
    }

    public Collection<Vinyl> getAllVinyls() {
        return vinyls.values();
    }

    public JsonArray getAllVinylsAsJSON() {
        JsonArray vinylArray = new JsonArray();

        for (Vinyl vinyl : vinyls.values()) {
            vinylArray.add(vinyl.toJSON());
        }

        return vinylArray;
    }

    public boolean reserveVinyl(int id, String clientId) {
        Vinyl vinyl = vinyls.get(id);
        if (vinyl != null && vinyl.getStatus() == VinylStatus.AVAILABLE) {
            vinyl.setStatus(VinylStatus.RESERVED);
            vinyl.setCurrentHolder(clientId);
            return true;
        }
        return false;
    }

    public boolean borrowVinyl(int id, String clientId) {
        Vinyl vinyl = vinyls.get(id);
        if (vinyl != null &&
                (vinyl.getStatus() == VinylStatus.AVAILABLE ||
                        (vinyl.getStatus() == VinylStatus.RESERVED && vinyl.getCurrentHolder().equals(clientId)))) {
            vinyl.setStatus(VinylStatus.BORROWED);
            vinyl.setCurrentHolder(clientId);
            return true;
        }
        return false;
    }

    public boolean returnVinyl(int id) {
        Vinyl vinyl = vinyls.get(id);
        if (vinyl != null && vinyl.getStatus() == VinylStatus.BORROWED) {
            vinyl.setStatus(VinylStatus.AVAILABLE);
            vinyl.setCurrentHolder("");
            return true;
        }
        return false;
    }

    public void addObserver(VinylObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(VinylObserver observer) {
        observers.remove(observer);
    }

    public void notifyStateChange(Vinyl vinyl) {
        for (VinylObserver observer : observers) {
            observer.onVinylStateChanged(vinyl);
        }
    }

    public void notifyVinylRemoved(int vinylId) {
        for (VinylObserver observer : observers) {
            observer.onVinylRemoved(vinylId);
        }
    }
}