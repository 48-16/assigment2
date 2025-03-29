package server;

public interface VinylObserver {
    void onVinylStateChanged(Vinyl vinyl);
    void onVinylRemoved(int vinylId);
}