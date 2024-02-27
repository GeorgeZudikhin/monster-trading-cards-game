package http.request;

import com.fasterxml.jackson.core.type.TypeReference;

import database.DatabaseUtil;
import gameElements.Card;
import http.response.ResponseModel;
import http.response.ResponseWriter;
import model.TradingDealModel;
import repository.*;
import repository.repositoryImpl.*;
import service.*;
import model.CardModel;
import model.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class SocketHandler implements Runnable {

    private final Socket clientConnection;
    private RequestRouter requestRouter;
    private final BufferedReader bufferedReader;
    private final ResponseWriter responseWriter;


    public SocketHandler(Socket clientConnection) throws IOException {
        this.clientConnection = clientConnection;
        bufferedReader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        responseWriter = new ResponseWriter(new BufferedWriter(new OutputStreamWriter(clientConnection.getOutputStream())));
    }

    @Override
    public void run() {
        try {
//            final String httpPath = bufferedReader.readLine();
//            final int offset = 0;
//
//            while (bufferedReader.ready()) {
//                final String input = bufferedReader.readLine();
//                if ("".equals(input)) {
//                    break;
//                }
//                headerParser.parseHeader(input);
//            }
            final String httpPath = RequestUtility.readHttpPath(bufferedReader);
            HeaderParser headerParser = RequestUtility.parseHeaders(bufferedReader);

            String requestBody = "";
            if (headerParser.getContentLength() > 0) {
                requestBody = RequestUtility.readRequestBody(bufferedReader, headerParser.getContentLength());
            }

            System.out.println("Thread: " + Thread.currentThread().getName());
            requestRouter = new RequestRouter(responseWriter, headerParser);
            requestRouter.routeRequest(httpPath, requestBody);
            responseWriter.closeConnection();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}