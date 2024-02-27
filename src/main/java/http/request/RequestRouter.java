package http.request;

import controller.*;
import database.DatabaseUtil;
import http.response.ResponseWriter;
import repository.BattleRepository;
import repository.CardRepository;
import repository.TradingRepository;
import repository.UserRepository;
import repository.repositoryImpl.BattleRepositoryImpl;
import repository.repositoryImpl.CardRepositoryImpl;
import repository.repositoryImpl.TradingRepositoryImpl;
import repository.repositoryImpl.UserRepositoryImpl;
import service.BattleService;
import service.CardService;
import service.TradingService;
import service.UserService;

import java.io.IOException;

public class RequestRouter {
    private final UserController userController;
    private final CardController cardController;
    private final BattleController battleController;
    private final TradingController tradingController;
    private final UserService userService;
    private final CardService cardService;
    private final BattleService battleService;
    private final TradingService tradingService;

    public RequestRouter(ResponseWriter responseWriter, HeaderParser headerParser) {
        DatabaseUtil databaseUtil = new DatabaseUtil();

        UserRepository userRepository = UserRepositoryImpl.getInstance(databaseUtil);
        CardRepository cardRepository = CardRepositoryImpl.getInstance(databaseUtil, userRepository);
        BattleRepository battleRepository = BattleRepositoryImpl.getInstance(databaseUtil);
        TradingRepository tradingRepository = TradingRepositoryImpl.getInstance(databaseUtil);
        this.userService = new UserService(userRepository, cardRepository);
        this.cardService = new CardService(userRepository, cardRepository);
        this.battleService = new BattleService(userRepository, cardRepository, battleRepository);
        this.tradingService = new TradingService(tradingRepository, userRepository, cardRepository);


        this.userController = new UserController(userService, responseWriter, headerParser);
        this.cardController = new CardController(cardService, userService, responseWriter, headerParser);
        this.battleController = new BattleController(battleService, responseWriter, headerParser);
        this.tradingController = new TradingController(tradingService, responseWriter, headerParser);
    }

    public void routeRequest(String httpPath, String requestBody) throws IOException {

        if (httpPath.startsWith("POST /users")) {
            userController.handleSignUp(requestBody);
        }

        else if (httpPath.startsWith("POST /sessions")) {
            userController.handleLogin(requestBody);
        }

        else if (httpPath.startsWith("POST /packages")) {
            cardController.handleCreationsOfPackages(requestBody);
        }

        else if (httpPath.startsWith("POST /transactions/packages")) {
            cardController.handleAcquisitionOfPackages();
        }

        else if (httpPath.startsWith("GET /cards")) {
            cardController.handleUserCardsRetrieval();
        }

        else if (httpPath.startsWith("GET /deck")) {
            String[] requestParts = httpPath.split(" ")[1].split("\\?", 2);
            String query = requestParts.length > 1 ? requestParts[1] : "";
            cardController.handleUserDeckRetrieval(query);
        }

        else if (httpPath.startsWith("PUT /deck")) {
            cardController.handleUserDeckUpdate(requestBody);
        }

        else if (httpPath.startsWith("GET /users/") || httpPath.startsWith("PUT /users/")) {
            String[] pathAndMethod = httpPath.split(" ");
            String[] pathSegments = pathAndMethod[1].split("/");
            String requestedUsername = pathSegments[2];

            if (httpPath.startsWith("GET /users/"))
                userController.handleUserRetrieval(requestedUsername);
            else if (httpPath.startsWith("PUT /users/"))
                userController.handleUserUpdate(requestBody, requestedUsername);
        }

        else if (httpPath.startsWith("GET /stats")) {
            battleController.handleUserStatsRetrieval();
        }

        else if (httpPath.startsWith("GET /scoreboard")) {
            battleController.handleScoreboardRetrieval();
        }

        else if (httpPath.startsWith("POST /battles")) {
            battleController.handleBattleStart();
        }

        else if (httpPath.startsWith("GET /tradings")) {
            tradingController.handleTradingDealsRetrieval();
        }

        else if (httpPath.equals("POST /tradings HTTP/1.1")) {
            tradingController.handleTradingDealCreation(requestBody);
        }

        else if (httpPath.startsWith("DELETE /tradings/")) {
            String[] pathAndMethod = httpPath.split(" ");
            String[] pathSegments = pathAndMethod[1].split("/");
            String dealID = pathSegments[2];

            tradingController.handleTradingDealDeletion(dealID);
        }

        else if (httpPath.matches("POST /tradings/.+")) {
            String[] pathAndMethod = httpPath.split(" ");
            String[] pathSegments = pathAndMethod[1].split("/");
            String dealID = pathSegments[2];

            tradingController.handleTradingDealAcceptance(requestBody, dealID);
        }

        else if (httpPath.startsWith("POST /gamble")) {
            cardController.handleGambling();
        }
    }
}
