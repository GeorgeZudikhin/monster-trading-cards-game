package server.requests;

import com.fasterxml.jackson.core.type.TypeReference;

import server.controller.BattleController;
import server.controller.CardsController;
import server.controller.UserController;
import server.models.CardModel;
import server.models.UserModel;
import server.response.ResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.response.ResponseModel;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class SocketHandler implements Runnable {

    private final Socket clientConnection;
    private final BufferedReader bufferedReader;
    private final ResponseHandler responseHandler;
    private final HeaderReader headerReader = new HeaderReader();
    private final ObjectMapper objectMapper = new ObjectMapper();
    UserController userController = new UserController();
    CardsController cardsController = new CardsController();
    BattleController battleController = new BattleController();

    public SocketHandler(Socket clientConnection) throws IOException {
        this.clientConnection = clientConnection;
        bufferedReader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        responseHandler = new ResponseHandler(new BufferedWriter(new OutputStreamWriter(clientConnection.getOutputStream())));
    }

    @Override
    public void run() {
        try {
            final String httpMethodWithPath = bufferedReader.readLine();
            System.out.println(httpMethodWithPath);

            while (bufferedReader.ready()) {
                final String input = bufferedReader.readLine();
                if ("".equals(input)) {
                    break;
                }
                headerReader.ingest(input);
            }

            headerReader.print();
            System.out.println("In thread: " + Thread.currentThread().getName());

            if (httpMethodWithPath.equals("POST /users HTTP/1.1")) {
                char[] charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());
                final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);
                ResponseModel responseModel = userController.regUser(userModel.getUsername(), userModel.getPassword());
                if (responseModel.getStatusCode() == 201) {
                    responseHandler.replyCreated(responseModel);
                } else if (responseModel.getStatusCode() == 409) {
                    responseHandler.replyConflict(responseModel);
                }

            } else if (httpMethodWithPath.equals("POST /sessions HTTP/1.1")) {
                char[] charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());

                final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);
                ResponseModel responseModel = userController.logUser(userModel.getUsername(), userModel.getPassword());
                if (responseModel.getStatusCode() == 200) {
                    responseHandler.replySuccessful(responseModel); // Successful login
                } else if (responseModel.getStatusCode() == 401) {
                    responseHandler.replyUnauthorized(responseModel); // Invalid credentials
                }

            } else if (httpMethodWithPath.equals("POST /packages HTTP/1.1")) {
                char[] charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());
                List<CardModel> cardsModel = objectMapper.readValue(new String(charBuffer), new TypeReference<>() {});

                ResponseModel responseModel = null;
                boolean allCardsCreated = true;

                for (CardModel card : cardsModel) {
                    responseModel = cardsController.generateNewCard(
                            card.getCardID(),
                            card.getCardName(),
                            card.getCardDamage(),
                            card.getCardElement(),
                            card.getPackageID(),
                            headerReader.getHeader("Authorization"));

                    if (responseModel.getStatusCode() != 201) {
                        allCardsCreated = false;
                        break;
                    }
                }
                if (allCardsCreated) {
                    responseHandler.replyCreated(responseModel);
                } else {
                    if (responseModel.getStatusCode() == 401) {
                        responseHandler.replyUnauthorized(responseModel);
                    } else if (responseModel.getStatusCode() == 403) {
                        responseHandler.replyForbidden(responseModel);
                    }
                }

            } else if (httpMethodWithPath.equals("POST /transactions/packages HTTP/1.1")) {
                int contentLength = headerReader.getContentLength();
                ResponseModel responseModel;

                if (contentLength > 0) {
                    char[] charBuffer = new char[contentLength];
                    bufferedReader.read(charBuffer, 0, contentLength);
                    final CardModel cardsModel = objectMapper.readValue(new String(charBuffer), CardModel.class);
                    responseModel = cardsController.acquirePackage(headerReader.getHeader("Authorization"));
                } else {
                    // Handle the case where there is no body content
                    responseModel = cardsController.acquirePackage(headerReader.getHeader("Authorization"));
                }

                if (responseModel.getStatusCode() == 200) {
                    responseHandler.replySuccessful(responseModel);
                } else if (responseModel.getStatusCode() == 403) {
                    responseHandler.replyForbidden(responseModel);
                } else if (responseModel.getStatusCode() == 404) {
                    responseHandler.replyNotFound(responseModel);
                }

            } else if (httpMethodWithPath.equals("GET /cards HTTP/1.1")) {
                Object response = userController.returnAllUserCards(headerReader.getHeader("Authorization"));
                responseHandler.reply(response);

            } else if (httpMethodWithPath.equals("GET /deck HTTP/1.1")) {
                Object response = userController.returnDeck(headerReader.getHeader("Authorization"));
                responseHandler.reply(response);

            } else if (httpMethodWithPath.equals("PUT /deck HTTP/1.1")) {
                String response = "Cards have not been added to deck";
                char[] charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());
                List<UserModel> userModel = objectMapper.readValue(new String(charBuffer), new TypeReference<>() {});
                System.out.println(userModel.size());

                int n = 0;
                for (UserModel e : userModel) {
                    if(userModel.size() < 4)
                        break;
                    response = userController.addToDeck(headerReader.getHeader("Authorization"), userModel.get(n).getCardID());
                    n++;
                }
                responseHandler.reply(response);

            } else if (httpMethodWithPath.equals("GET /deck?format=plain HTTP/1.1")) {
                Object response = userController.returnDeck(headerReader.getHeader("Authorization"));
                responseHandler.reply(response);

            } else if (httpMethodWithPath.equals("GET /stats HTTP/1.1")) {
                String response = userController.returnEloScore(headerReader.getHeader("Authorization"));
                responseHandler.reply(response);

            } else if (httpMethodWithPath.equals("GET /ratio HTTP/1.1")) {
                String response = userController.returnWinLossRatio(headerReader.getHeader("Authorization"));
                responseHandler.reply(response);

            } else if (httpMethodWithPath.equals("GET /score HTTP/1.1")) {
                Object response = userController.returnGlobalScoreboard(headerReader.getHeader("Authorization"));
                responseHandler.reply(response);

            } else if (httpMethodWithPath.contains("users/")) {
                String response;

                if (headerReader.getContentLength() == 0)
                    response = "Please provide Data to update";
                else {
                    char[] charBuffer = new char[headerReader.getContentLength()];
                    bufferedReader.read(charBuffer, 0, headerReader.getContentLength());
                    final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);

                    response = userController.updateUserProfile(headerReader.getHeader("Authorization"), userModel.getNewUsername(), userModel.getNewBio(), userModel.getNewImage(), httpMethodWithPath);
                }
                responseHandler.reply(response);

            } else if (httpMethodWithPath.equals("POST /battles HTTP/1.1")) {
                String response = battleController.startBattleIfTwoUsersAreReady(headerReader.getHeader("Authorization"));
                responseHandler.reply(response);

            } else if (httpMethodWithPath.contains("tradings")) {
                String response = "Tradings is not available";
                responseHandler.reply(response);
            }
            responseHandler.reply();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}