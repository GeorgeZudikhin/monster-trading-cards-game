package businessLogic;

import database.DataBase;
import http.ResponseModel;
import service.UserService;

import java.util.List;

public class InitializeBattle {

    public ResponseModel beginBattle(String playerAName, String playerBName) {
        DataBase myData = new DataBase();
        //User
        User playerA = new User(playerAName);
        User playerB = new User(playerBName);

        playerA.setCoins(myData.returnUserCoins(playerAName));
        playerA.setElo(myData.returnElo(playerAName));
        playerA.setWins(myData.returnUserWins(playerAName));
        playerA.setLosses(myData.returnUserLosses(playerAName));
        playerB.setCoins(myData.returnUserCoins(playerBName));
        playerB.setElo(myData.returnElo(playerBName));
        playerB.setWins(myData.returnUserWins(playerBName));
        playerB.setLosses(myData.returnUserLosses(playerBName));

        //add to deck playerA
        List<Card> playerACards = UserService.generateUserDeck(playerA.getUsername());
        playerA.setDeck(playerACards);
        System.out.println(playerACards.size());


        //add to deck playerB
        List<Card> playerBCards = UserService.generateUserDeck(playerB.getUsername());
        playerB.setDeck(playerBCards);
        System.out.println(playerBCards.size());

        BattleManager.startGame(playerACards, playerBCards, playerA, playerB);
        System.out.println("##############################################");
        System.out.println(playerA.getUsername() + " " + playerA.getWinCounter() + " wins");
        System.out.println(playerB.getUsername() + " " + playerB.getWinCounter() + " wins");
        System.out.println("##############################################");
        if (playerA.getWinCounter() > playerB.getWinCounter()) {
            myData.countWins(playerAName, playerA.getWins() + 1);
            myData.countLosses(playerBName, playerB.getLosses() + 1);
            myData.updateElo(playerA.getUsername(), playerA.getElo() + 3);
            myData.updateElo(playerB.getUsername(), playerB.getElo() - 5);
            return new ResponseModel(playerA.getUsername() + " " + "has won the game!", 200);
            //return (playerA.getUsername() + " " + "is the winner of the Game");
        } else if (playerA.getWinCounter() < playerB.getWinCounter()) {
            myData.countLosses(playerAName, playerA.getLosses() + 1);
            myData.countWins(playerBName, playerB.getWins() + 1);
            myData.updateElo(playerA.getUsername(), playerA.getElo() - 5);
            myData.updateElo(playerB.getUsername(), playerB.getElo() + 3);
            return new ResponseModel(playerB.getUsername() + " " + "has won the game!", 200);
        } else {
            return new ResponseModel("Draw!", 200);
        }
    }
}
