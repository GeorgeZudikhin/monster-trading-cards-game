package server;

import http.request.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    protected static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        int port = 10001;
        int numberOfThreads = 10;
        int backlog = 5;

        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // "try-with-resources"
        try (ServerSocket serverSocket = new ServerSocket(port, backlog)) {
            System.out.println("Server listening...");
            while (!serverSocket.isClosed()) {
                final Socket clientConnection = serverSocket.accept();

                final SocketHandler socketHandler = new SocketHandler(clientConnection);
                System.out.println("New connection from port " + port + "...");
                executorService.submit(socketHandler);
            }
        } catch (IOException e) {
            logger.error("Server exception: " + e.getMessage(), e);
        } finally {
            executorService.shutdown();
        }
    }
}
