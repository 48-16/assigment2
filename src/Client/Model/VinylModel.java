package Client.Model;

import javafx.beans.property.*;
import com.google.gson.JsonObject;

public class VinylModel {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty artist = new SimpleStringProperty();
    private StringProperty genre = new SimpleStringProperty();
    private IntegerProperty year = new SimpleIntegerProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty currentHolder = new SimpleStringProperty();

    public VinylModel(int id, String title, String artist, String genre, int year, String status, String currentHolder) {
        this.id.set(id);
        this.title.set(title);
        this.artist.set(artist);
        this.genre.set(genre);
        this.year.set(year);
        this.status.set(status);
        this.currentHolder.set(currentHolder);
    }

    public VinylModel(JsonObject jsonObject) {
        this.id.set(jsonObject.get("id").getAsInt());
        this.title.set(jsonObject.get("title").getAsString());
        this.artist.set(jsonObject.get("artist").getAsString());
        this.genre.set(jsonObject.get("genre").getAsString());
        this.year.set(jsonObject.get("year").getAsInt());
        this.status.set(jsonObject.get("status").getAsString());
        this.currentHolder.set(jsonObject.get("currentHolder").getAsString());
    }

    // Getters and setters for properties
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty currentHolderProperty() {
        return currentHolder;
    }

    // Regular getters
    public int getId() {
        return id.get();
    }

    public String getTitle() {
        return title.get();
    }

    public String getArtist() {
        return artist.get();
    }

    public String getGenre() {
        return genre.get();
    }

    public int getYear() {
        return year.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getCurrentHolder() {
        return currentHolder.get();
    }

    // Setters
    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setCurrentHolder(String currentHolder) {
        this.currentHolder.set(currentHolder);
    }
}
