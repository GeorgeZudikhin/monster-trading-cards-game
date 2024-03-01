package service;

import gameManager.BattleInitiator;
import gameManager.BattleLockManager;
import http.response.ResponseModel;
import model.StatsModel;
import repository.BattleRepository;
import repository.CardRepository;
import repository.UserRepository;

import java.util.List;

public class BattleService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BattleRepository battleRepository;
    public BattleService(UserRepository userRepository, CardRepository cardRepository, BattleRepository battleRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.battleRepository = battleRepository;
    }

    public ResponseModel returnUserStats(String authToken) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        String username = userRepository.returnUsernameFromToken(authToken);

        StatsModel stats = battleRepository.returnUserStats(username);
        if(stats == null)
            return new ResponseModel("No stats could be retrieved", 404);

        return new ResponseModel("The stats could be retrieved successfully", 200, stats);
    }

    public ResponseModel returnScoreboard(String authToken) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        List<StatsModel> scoreboard = battleRepository.returnScoreboard();
        if(scoreboard == null)
            return new ResponseModel("No scoreboard could be retrieved", 404);

        return new ResponseModel("The scoreboard could be retrieved successfully", 200, scoreboard);
    }

    public synchronized ResponseModel startBattle(String authToken) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        battleRepository.setPlayerToBeReadyToPlay(userID);

        int amountOfPlayersReady = battleRepository.returnHowManyPlayersAreReady();

        if (amountOfPlayersReady < 2)
            return new ResponseModel("Player One Ready, waiting for Player Two", 403);

        if (!BattleLockManager.tryAcquireBattle())
            return new ResponseModel("One player cannot start a battle", 503);

        try {
            List<String> readyPlayers = battleRepository.returnUsernamesOfPlayersReady();

            String playerA = readyPlayers.get(0);
            String playerB = readyPlayers.get(1);

            BattleInitiator battleInitiator = new BattleInitiator(userRepository, cardRepository, battleRepository);
            return battleInitiator.initializeBattle(playerA, playerB);
        } finally {
            BattleLockManager.releaseBattle();
            battleRepository.resetUserReadyStatus();
        }
    }
}
