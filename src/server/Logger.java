package server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static Logger instance;
    private static final String LOG_FILE_PREFIX = "server_log_";
    private PrintWriter fileWriter;

    private Logger() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = LOG_FILE_PREFIX + dateFormat.format(new Date()) + ".txt";
            fileWriter = new PrintWriter(new FileWriter(fileName, true), true);
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String logMessage = timestamp + " - " + message;

        // Log to console
        System.out.println(logMessage);

        // Log to file
        if (fileWriter != null) {
            fileWriter.println(logMessage);
        }
    }

    public void close() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}