package server;

import server.Server;
public class Main {
    public static void main(String[] args) {
        try {
            Server.main(args);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
