package server;

import server.requests.SocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 10001;

        final ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(port, 5)) {
            System.out.println("Listening on port " + port + "...");
            while (true) {
                final Socket clientConnection = serverSocket.accept();

                final SocketHandler socketHandler = new SocketHandler(clientConnection);
                System.out.println("New connection on port " + port + "...");
                executorService.submit(socketHandler);
            }
        }
    }
}
