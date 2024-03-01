package http.request;

import http.response.ResponseWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private final BufferedReader bufferedReader;
    private final ResponseWriter responseWriter;
    protected static final Logger logger = LogManager.getLogger();

    public SocketHandler(Socket clientSocket) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        responseWriter = new ResponseWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
    }

    @Override
    public void run() {
        try {
            final String httpPath = RequestUtility.readHttpPath(bufferedReader);
            HeaderParser headerParser = RequestUtility.parseHeaders(bufferedReader);

            String requestBody = "";
            if (headerParser.getContentLength() > 0) {
                requestBody = RequestUtility.readRequestBody(bufferedReader, headerParser.getContentLength());
            }

            logger.info("Current thread: " + Thread.currentThread().getName());
            RequestRouter requestRouter = new RequestRouter(responseWriter, headerParser);
            requestRouter.routeRequest(httpPath, requestBody);
            responseWriter.closeConnection();
        } catch (IOException e) {
            logger.error("I/O error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage(), e);
        }
    }
}