package http;

import com.fasterxml.jackson.core.type.TypeReference;

import businessLogic.Card;
import repository.*;
import repositoryImpl.*;
import service.BattleService;
import service.CardService;
import service.UserService;
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
    private final BufferedReader bufferedReader;
    private final ResponseHandler responseHandler;
    private final HeaderReader headerReader = new HeaderReader();
    private final ObjectMapper objectMapper = new ObjectMapper();
    //UserController userController = new UserController();
    private final UserService userService;
    //CardService cardService = new CardService();
    private final CardService cardService;
    //BattleService battleService = new BattleService();
    private final BattleService battleService;

    public SocketHandler(Socket clientConnection) throws IOException {
        this.clientConnection = clientConnection;
        bufferedReader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        responseHandler = new ResponseHandler(new BufferedWriter(new OutputStreamWriter(clientConnection.getOutputStream())));

        UserRepository userRepository = UserRepositoryImpl.getInstance();
        CardRepository cardRepository = CardRepositoryImpl.getInstance(userRepository);
        BattleRepository battleRepository = BattleRepositoryImpl.getInstance();
        this.userService = new UserService(userRepository, cardRepository);
        this.cardService = new CardService(userRepository, cardRepository);
        this.battleService = new BattleService(userRepository, battleRepository);
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
                ResponseModel responseModel = userService.signUpUser(userModel.getUsername(), userModel.getPassword());

                switch (responseModel.getStatusCode()) {
                    case 201 -> responseHandler.replyCreated(responseModel);
                    case 409 -> responseHandler.replyConflict(responseModel);
                }

            } else if (httpMethodWithPath.equals("POST /sessions HTTP/1.1")) {
                char[] charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());

                final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);
                ResponseModel responseModel = userService.logInUser(userModel.getUsername(), userModel.getPassword());

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseHandler.replySuccessful(responseModel);
                    case 401 -> responseHandler.replyUnauthorized(responseModel);
                }

            } else if (httpMethodWithPath.equals("POST /packages HTTP/1.1")) {
                char[] charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());
                List<CardModel> cardsModel = objectMapper.readValue(new String(charBuffer), new TypeReference<>() {});

                ResponseModel responseModel = null;
                boolean allCardsCreated = true;

                for (CardModel card : cardsModel) {
                    responseModel = cardService.generateNewCard(
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
                    switch (responseModel.getStatusCode()) {
                        case 401 -> responseHandler.replyUnauthorized(responseModel);
                        case 403 -> responseHandler.replyForbidden(responseModel);
                    }
                }

            } else if (httpMethodWithPath.equals("POST /transactions/packages HTTP/1.1")) {
                ResponseModel responseModel = cardService.acquirePackage(headerReader.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseHandler.replySuccessful(responseModel);
                    case 403 -> responseHandler.replyForbidden(responseModel);
                    case 404 -> responseHandler.replyNotFound(responseModel);
                }

            } else if (httpMethodWithPath.equals("GET /cards HTTP/1.1")) {
                ResponseModel responseModel = userService.returnAllUserCards(headerReader.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseHandler.replySuccessful(responseModel);
                    case 401 -> responseHandler.replyUnauthorized(responseModel);
                    case 204 -> responseHandler.replyNoContent(responseModel);
                }

            } else if (httpMethodWithPath.startsWith("GET /deck")) {
                String[] requestParts = httpMethodWithPath.split(" ")[1].split("\\?", 2);
                String path = requestParts[0];
                String query = requestParts.length > 1 ? requestParts[1] : "";

                ResponseModel responseModel = userService.returnUserDeck(headerReader.getHeader("Authorization"));

                if (responseModel.getStatusCode() == 200) {
                    if (query.contains("format=plain")) {
                        List<Card> deck = (List<Card>) responseModel.getResponseBody();
                        String plainResponse = deck.stream()
                                .map(Card::toString)
                                .collect(Collectors.joining("\n"));

                        responseHandler.replyInPlainText(responseModel, plainResponse);
                    } else {
                        responseHandler.replySuccessful(responseModel);
                    }
                } else if (responseModel.getStatusCode() == 401) {
                    responseHandler.replyUnauthorized(responseModel);
                } else if (responseModel.getStatusCode() == 404) {
                    responseHandler.replyNotFound(responseModel);
                }
            } else if (httpMethodWithPath.equals("PUT /deck HTTP/1.1")) {
                char[] charBuffer = new char[headerReader.getContentLength()];
                bufferedReader.read(charBuffer, 0, headerReader.getContentLength());
                List<String> cardIDs = objectMapper.readValue(new String(charBuffer), new TypeReference<>() {});
                ResponseModel responseModel = null;
                boolean forbidden = false;

                if(cardIDs.size() < 4) {
                    responseHandler.replyBadRequest(new ResponseModel("The provided deck did not include the required amount of cards", 400));
                } else {
                    for (String cardID : cardIDs) {
                        responseModel = userService.addCardToDeck(headerReader.getHeader("Authorization"), cardID);
                        if (responseModel.getStatusCode() == 403) {
                            forbidden = true;
                            break;
                        }
                    }
                    if (forbidden) {
                        responseHandler.replyForbidden(responseModel);
                    } else if (responseModel.getStatusCode() == 200) {
                        responseHandler.replySuccessful(responseModel);
                    } else if (responseModel.getStatusCode() == 401) {
                        responseHandler.replyUnauthorized(responseModel);
                    }
                }

            } else if (httpMethodWithPath.startsWith("GET /users/") || httpMethodWithPath.startsWith("PUT /users/")) {
                ResponseModel responseModel = null;

                String[] pathAndMethod = httpMethodWithPath.split(" ");
                String[] pathSegments = pathAndMethod[1].split("/");
                String requestedUsername = pathSegments[2];
                System.out.println(requestedUsername);

                if (httpMethodWithPath.startsWith("GET /users/")) {
                    responseModel = userService.getUserProfileByUsername(headerReader.getHeader("Authorization"), requestedUsername);
                } else {
                    if (headerReader.getContentLength() == 0) {
                        responseModel = new ResponseModel("User not found", 400);
                    } else {
                        char[] charBuffer = new char[headerReader.getContentLength()];
                        bufferedReader.read(charBuffer, 0, headerReader.getContentLength());
                        final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);

                        responseModel = userService.updateUserProfile(headerReader.getHeader("Authorization"), requestedUsername, userModel.getNewUsername(), userModel.getNewBio(), userModel.getNewImage());
                    }
                }

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseHandler.replySuccessful(responseModel);
                    case 400 -> responseHandler.replyBadRequest(responseModel);
                    case 401 -> responseHandler.replyUnauthorized(responseModel);
                    case 403 -> responseHandler.replyForbidden(responseModel);
                    case 404 -> responseHandler.replyNotFound(responseModel);
                }
            } else if (httpMethodWithPath.equals("GET /stats HTTP/1.1")) {
                ResponseModel responseModel = battleService.returnUserStats(headerReader.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseHandler.replySuccessful(responseModel);
                    case 401 -> responseHandler.replyUnauthorized(responseModel);
                    case 404 -> responseHandler.replyNotFound(responseModel);
                }

            } else if (httpMethodWithPath.equals("GET /scoreboard HTTP/1.1")) {
                ResponseModel responseModel = battleService.returnScoreboard(headerReader.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseHandler.replySuccessful(responseModel);
                    case 401 -> responseHandler.replyUnauthorized(responseModel);
                    case 404 -> responseHandler.replyNotFound(responseModel);
                }

            } else if (httpMethodWithPath.equals("POST /battles HTTP/1.1")) {
                String response = battleService.startBattleIfTwoUsersAreReady(headerReader.getHeader("Authorization"));
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