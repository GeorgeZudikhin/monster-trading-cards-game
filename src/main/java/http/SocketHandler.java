package http;

import com.fasterxml.jackson.core.type.TypeReference;

import database.DatabaseUtil;
import gameElements.Card;
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
    private final BufferedReader bufferedReader;
    private final ResponseWriter responseWriter;
    private final HeaderParser headerParser = new HeaderParser();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;
    private final CardService cardService;
    private final BattleService battleService;
    private final TradingService tradingService;

    public SocketHandler(Socket clientConnection) throws IOException {
        this.clientConnection = clientConnection;
        bufferedReader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        responseWriter = new ResponseWriter(new BufferedWriter(new OutputStreamWriter(clientConnection.getOutputStream())));

        DatabaseUtil databaseUtil = new DatabaseUtil();

        UserRepository userRepository = UserRepositoryImpl.getInstance(databaseUtil);
        CardRepository cardRepository = CardRepositoryImpl.getInstance(databaseUtil, userRepository);
        BattleRepository battleRepository = BattleRepositoryImpl.getInstance(databaseUtil);
        TradingRepository tradingRepository = TradingRepositoryImpl.getInstance(databaseUtil);
        this.userService = new UserService(userRepository, cardRepository);
        this.cardService = new CardService(userRepository, cardRepository);
        this.battleService = new BattleService(userRepository, cardRepository, battleRepository);
        this.tradingService = new TradingService(tradingRepository, userRepository, cardRepository);
    }

    @Override
    public void run() {
        try {
            final String httpPath = bufferedReader.readLine();
            final int offset = 0;

            while (bufferedReader.ready()) {
                final String input = bufferedReader.readLine();
                if ("".equals(input)) {
                    break;
                }
                headerParser.parseHeader(input);
            }

            System.out.println("Thread: " + Thread.currentThread().getName());

            if (httpPath.startsWith("POST /users")) {
                char[] charBuffer = new char[headerParser.getContentLength()];
                bufferedReader.read(charBuffer, offset, headerParser.getContentLength());
                final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);
                ResponseModel responseModel = userService.signUpUser(userModel.getUsername(), userModel.getPassword());

                switch (responseModel.getStatusCode()) {
                    case 201 -> responseWriter.replyCreated(responseModel);
                    case 409 -> responseWriter.replyConflict(responseModel);
                }

            } else if (httpPath.startsWith("POST /sessions")) {
                char[] charBuffer = new char[headerParser.getContentLength()];
                bufferedReader.read(charBuffer, offset, headerParser.getContentLength());

                final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);
                ResponseModel responseModel = userService.logInUser(userModel.getUsername(), userModel.getPassword());

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                }

            } else if (httpPath.startsWith("POST /packages")) {
                char[] charBuffer = new char[headerParser.getContentLength()];
                bufferedReader.read(charBuffer, offset, headerParser.getContentLength());
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
                            headerParser.getHeader("Authorization"));

                    if (responseModel.getStatusCode() != 201) {
                        allCardsCreated = false;
                        break;
                    }
                }
                if (allCardsCreated) {
                    responseWriter.replyCreated(responseModel);
                } else {
                    switch (responseModel.getStatusCode()) {
                        case 401 -> responseWriter.replyUnauthorized(responseModel);
                        case 403 -> responseWriter.replyForbidden(responseModel);
                    }
                }

            } else if (httpPath.startsWith("POST /transactions/packages")) {
                ResponseModel responseModel = cardService.acquirePackage(headerParser.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 403 -> responseWriter.replyForbidden(responseModel);
                    case 404 -> responseWriter.replyNotFound(responseModel);
                }

            } else if (httpPath.startsWith("GET /cards")) {
                ResponseModel responseModel = userService.returnAllUserCards(headerParser.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 204 -> responseWriter.replyNoContent(responseModel);
                }

            } else if (httpPath.startsWith("GET /deck")) {
                String[] requestParts = httpPath.split(" ")[1].split("\\?", 2);
                String query = requestParts.length > 1 ? requestParts[1] : "";

                ResponseModel responseModel = userService.returnUserDeck(headerParser.getHeader("Authorization"));

                if (responseModel.getStatusCode() == 200) {
                    if (query.contains("format=plain")) {
                        List<Card> deck = (List<Card>) responseModel.getResponseBody();
                        String plainResponse = deck.stream()
                                .map(Card::toString)
                                .collect(Collectors.joining("\n"));

                        responseWriter.replyInPlainText(responseModel, plainResponse);
                    } else {
                        responseWriter.replySuccessful(responseModel);
                    }
                } else if (responseModel.getStatusCode() == 401) {
                    responseWriter.replyUnauthorized(responseModel);
                } else if (responseModel.getStatusCode() == 404) {
                    responseWriter.replyNotFound(responseModel);
                }
            } else if (httpPath.startsWith("PUT /deck")) {
                char[] charBuffer = new char[headerParser.getContentLength()];
                bufferedReader.read(charBuffer, offset, headerParser.getContentLength());
                List<String> cardIDs = objectMapper.readValue(new String(charBuffer), new TypeReference<>() {});
                ResponseModel responseModel = null;
                boolean forbidden = false;

                if(cardIDs.size() < 4) {
                    responseWriter.replyBadRequest(new ResponseModel("The provided deck did not include the required amount of cards", 400));
                } else {
                    for (String cardID : cardIDs) {
                        responseModel = userService.addCardToDeck(headerParser.getHeader("Authorization"), cardID);
                        if (responseModel.getStatusCode() == 403) {
                            forbidden = true;
                            break;
                        }
                    }
                    if (forbidden) {
                        responseWriter.replyForbidden(responseModel);
                    } else if (responseModel.getStatusCode() == 200) {
                        responseWriter.replySuccessful(responseModel);
                    } else if (responseModel.getStatusCode() == 401) {
                        responseWriter.replyUnauthorized(responseModel);
                    }
                }

            } else if (httpPath.startsWith("GET /users/") || httpPath.startsWith("PUT /users/")) {
                ResponseModel responseModel;

                String[] pathAndMethod = httpPath.split(" ");
                String[] pathSegments = pathAndMethod[1].split("/");
                String requestedUsername = pathSegments[2];
                System.out.println(requestedUsername);

                if (httpPath.startsWith("GET /users/")) {
                    responseModel = userService.getUserProfileByUsername(headerParser.getHeader("Authorization"), requestedUsername);
                } else {
                    if (headerParser.getContentLength() == 0) {
                        responseModel = new ResponseModel("User not found", 400);
                    } else {
                        char[] charBuffer = new char[headerParser.getContentLength()];
                        bufferedReader.read(charBuffer, offset, headerParser.getContentLength());
                        final UserModel userModel = objectMapper.readValue(new String(charBuffer), UserModel.class);

                        responseModel = userService.updateUserProfile(headerParser.getHeader("Authorization"), requestedUsername, userModel.getNewUsername(), userModel.getNewBio(), userModel.getNewImage());
                    }
                }

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 400 -> responseWriter.replyBadRequest(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 403 -> responseWriter.replyForbidden(responseModel);
                    case 404 -> responseWriter.replyNotFound(responseModel);
                }
            } else if (httpPath.startsWith("GET /stats")) {
                ResponseModel responseModel = battleService.returnUserStats(headerParser.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 404 -> responseWriter.replyNotFound(responseModel);
                }

            } else if (httpPath.startsWith("GET /scoreboard")) {
                ResponseModel responseModel = battleService.returnScoreboard(headerParser.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 404 -> responseWriter.replyNotFound(responseModel);
                }

            } else if (httpPath.startsWith("POST /battles")) {
                ResponseModel responseModel = battleService.startBattle(headerParser.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 403 -> responseWriter.replyForbidden(responseModel);
                }

            } else if (httpPath.startsWith("GET /tradings")) {
                ResponseModel responseModel = tradingService.getTradingDeals(headerParser.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 404 -> responseWriter.replyNotFound(responseModel);
                }
            } else if (httpPath.equals("POST /tradings HTTP/1.1")) {
                char[] charBuffer = new char[headerParser.getContentLength()];
                bufferedReader.read(charBuffer, offset, headerParser.getContentLength());
                final TradingDealModel tradingDeal = objectMapper.readValue(new String(charBuffer), TradingDealModel.class);
                ResponseModel responseModel = tradingService.createTradingDeal(headerParser.getHeader("Authorization"), tradingDeal);

                switch (responseModel.getStatusCode()) {
                    case 201 -> responseWriter.replyCreated(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 409 -> responseWriter.replyConflict(responseModel);
                }
            } else if (httpPath.startsWith("DELETE /tradings/")) {
                String[] pathAndMethod = httpPath.split(" ");
                String[] pathSegments = pathAndMethod[1].split("/");
                String dealID = pathSegments[2];
                System.out.println(dealID);

                ResponseModel responseModel = tradingService.deleteTradingDeal(headerParser.getHeader("Authorization"), dealID);
                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                }
            } else if (httpPath.matches("POST /tradings/.+")) {
                String[] pathAndMethod = httpPath.split(" ");
                String[] pathSegments = pathAndMethod[1].split("/");
                String dealID = pathSegments[2];
                System.out.println("Deal ID: " + dealID);

                char[] charBuffer = new char[headerParser.getContentLength()];
                bufferedReader.read(charBuffer, offset, headerParser.getContentLength());
                final String cardID = objectMapper.readValue(new String(charBuffer), String.class);
                System.out.println("Card ID: " + cardID);

                ResponseModel responseModel = tradingService.acceptTradingDeal(headerParser.getHeader("Authorization"), dealID, cardID);

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 400 -> responseWriter.replyBadRequest(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 403 -> responseWriter.replyForbidden(responseModel);
                    case 404 -> responseWriter.replyNotFound(responseModel);
                }
            } else if (httpPath.startsWith("POST /gamble")) {
                ResponseModel responseModel = cardService.gambleCard(headerParser.getHeader("Authorization"));

                switch (responseModel.getStatusCode()) {
                    case 200 -> responseWriter.replySuccessful(responseModel);
                    case 204 -> responseWriter.replyNoContent(responseModel);
                    case 401 -> responseWriter.replyUnauthorized(responseModel);
                    case 500 -> responseWriter.replyInternalServerError(responseModel);
                }
            }
            responseWriter.closeConnection();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}