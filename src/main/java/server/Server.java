package server;

import http.request.SocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        int port = 10001;

        final ExecutorService executorService = Executors.newFixedThreadPool(10);

        // "try-with-resources"
        try (ServerSocket serverSocket = new ServerSocket(port, 5)) {
            System.out.println("Server listening...");
            while (!serverSocket.isClosed()) {
                final Socket clientConnection = serverSocket.accept();

                final SocketHandler socketHandler = new SocketHandler(clientConnection);
                System.out.println("New connection from port " + port + "...");
                executorService.submit(socketHandler);
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }
}
