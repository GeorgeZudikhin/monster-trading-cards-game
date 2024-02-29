package http.request;

import http.response.ResponseWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketHandler implements Runnable {

    private final Socket clientSocket;
    private final BufferedReader bufferedReader;
    private final ResponseWriter responseWriter;


    public SocketHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
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

            System.out.println("Current thread: " + Thread.currentThread().getName());
            RequestRouter requestRouter = new RequestRouter(responseWriter, headerParser);
            requestRouter.routeRequest(httpPath, requestBody);
            responseWriter.closeConnection();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}