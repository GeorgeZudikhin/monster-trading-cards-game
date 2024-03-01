package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    protected static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        try {
            Server.main(args);
        } catch (Exception e) {
            logger.error("Failed to start server: " + e.getMessage(), e);
        }
    }
}
