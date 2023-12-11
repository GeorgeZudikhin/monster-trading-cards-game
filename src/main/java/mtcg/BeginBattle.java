package mtcg;

import database.DataBase;
import server.controller.UserController;

import java.util.List;

public class BeginBattle {

    public String beginBattle(String playerAName, String playerBName) {
        DataBase myData = new DataBase();
        //User
        User playerA = new User(playerAName);
        User playerB = new User(playerBName);

        playerA.setCoins(myData.returnCoins(playerAName));
        playerA.setElo(myData.returnElo(playerAName));
        playerA.setWins(myData.returnWins(playerAName));
        playerA.setLosses(myData.returnLosses(playerAName));
        playerB.setCoins(myData.returnCoins(playerBName));
        playerB.setElo(myData.returnElo(playerBName));
        playerB.setWins(myData.returnWins(playerBName));
        playerB.setLosses(myData.returnLosses(playerBName));

        //add to deck playerA
        List<Card> playerACards = UserController.generateUserDeck(playerA.getUsername());
        playerA.setDeck(playerACards);
        System.out.println(playerACards.size());


        //add to deck playerB
        List<Card> playerBCards = UserController.generateUserDeck(playerB.getUsername());
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
            return (playerA.getUsername() + " " + "is the winner of the Game");
        } else if (playerA.getWinCounter() < playerB.getWinCounter()) {
            myData.countLosses(playerAName, playerA.getLosses() + 1);
            myData.countWins(playerBName, playerB.getWins() + 1);
            myData.updateElo(playerA.getUsername(), playerA.getElo() - 5);
            myData.updateElo(playerB.getUsername(), playerB.getElo() + 3);
            return playerB.getUsername() + " " + "is the winner of the Game";
        } else {
            return "Draw";
        }
    }
}
