package service;

import businessLogic.InitializeBattle;
import database.DataBase;

import java.util.List;

public class BattleService {
    DataBase myData = new DataBase();

    public String startBattleIfTwoUsersAreReady(String token) {
        String username = myData.returnUsernameFromToken(token);
        myData.readyToPlay(token, 1);

        int readyPlayer = myData.returnPlayerReady(username);

        if (readyPlayer == 2) {
            List<String> readyPlayers = myData.returnUsernamePlayerReady();

            String playerA = readyPlayers.get(0);
            String playerB = readyPlayers.get(1);

            InitializeBattle initializeBattle = new InitializeBattle();
            return initializeBattle.beginBattle(playerA, playerB);
        }
        return "Player in Lobby";
    }
}
