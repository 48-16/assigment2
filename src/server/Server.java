package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Server {
    private static final int PORT = 8888;
    private ServerSocket serverSocket;
    private VinylRepository vinylRepository;
    private Map<Socket, ClientHandler> clientHandlers;
    private static Logger logger = Logger.getInstance();
    private Gson gson;

    public Server() {
        vinylRepository = new VinylRepository();
        clientHandlers = new ConcurrentHashMap<>();
        gson = new Gson();
        initializeVinylRepository();
    }

    private void initializeVinylRepository() {
        // Add some sample vinyls to the repository
        vinylRepository.addVinyl(new Vinyl(1, "Thriller", "Michael Jackson", "Pop", 1982));
        vinylRepository.addVinyl(new Vinyl(2, "Back in Black", "AC/DC", "Rock", 1980));
        vinylRepository.addVinyl(new Vinyl(3, "The Dark Side of the Moon", "Pink Floyd", "Progressive Rock", 1973));
        vinylRepository.addVinyl(new Vinyl(4, "Abbey Road", "The Beatles", "Rock", 1969));
        vinylRepository.addVinyl(new Vinyl(5, "Rumours", "Fleetwood Mac", "Rock", 1977));
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            logger.log("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.log("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandlers.put(clientSocket, clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            logger.log("Error in server: " + e.getMessage());
        }
    }

    public void removeClient(Socket clientSocket) {
        clientHandlers.remove(clientSocket);
        logger.log("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
    }

    public void broadcastMessage(JsonObject message, Socket excludeSocket) {
        String jsonMessage = gson.toJson(message);
        for (Socket socket : clientHandlers.keySet()) {
            if (socket != excludeSocket) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(jsonMessage);
                } catch (IOException e) {
                    logger.log("Error broadcasting message: " + e.getMessage());
                }
            }
        }
    }

    public VinylRepository getVinylRepository() {
        return vinylRepository;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}