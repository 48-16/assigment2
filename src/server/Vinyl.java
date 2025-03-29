package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Vinyl {
    private int id;
    private String title;
    private String artist;
    private String genre;
    private int year;
    private VinylStatus status;
    private String currentHolder;

    public Vinyl(int id, String title, String artist, String genre, int year) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.year = year;
        this.status = VinylStatus.AVAILABLE;
        this.currentHolder = "";
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public VinylStatus getStatus() {
        return status;
    }

    public void setStatus(VinylStatus status) {
        this.status = status;
    }

    public String getCurrentHolder() {
        return currentHolder;
    }

    public void setCurrentHolder(String currentHolder) {
        this.currentHolder = currentHolder;
    }

    public JsonObject toJSON() {
        Gson gson = new Gson();
        return gson.toJsonTree(this).getAsJsonObject();
    }
}