package gameLogic;

import gameElements.Card;
import gameElements.User;
import http.ResponseModel;
import repository.CardRepository;
import repository.UserRepository;

import java.util.List;

public class BattleInitializer {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public BattleInitializer(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public ResponseModel beginBattle(String playerAName, String playerBName) {
        User playerA = new User(playerAName);
        User playerB = new User(playerBName);

        playerA.setCoins(userRepository.returnUserCoins(playerAName));
        playerA.setElo(userRepository.getUserEloByUsername(playerAName));
        playerA.setWins(userRepository.getUserWinsByUsername(playerAName));
        playerA.setLosses(userRepository.getUserLossesByUsername(playerAName));

        playerB.setCoins(userRepository.returnUserCoins(playerBName));
        playerB.setElo(userRepository.getUserEloByUsername(playerBName));
        playerB.setWins(userRepository.getUserWinsByUsername(playerBName));
        playerB.setLosses(userRepository.getUserLossesByUsername(playerBName));

        //add to deck playerA
        List<Card> playerACards = cardRepository.getUserDeckByUserID(userRepository.returnUserIDFromUsername(playerA.getUsername()));
        playerA.setDeck(playerACards);
        System.out.println(playerACards.size());


        //add to deck playerB
        List<Card> playerBCards = cardRepository.getUserDeckByUserID(userRepository.returnUserIDFromUsername(playerB.getUsername()));
        playerB.setDeck(playerBCards);
        System.out.println(playerBCards.size());

        BattleManager.startGame(playerACards, playerBCards, playerA, playerB);
        System.out.println("##############################################");
        System.out.println(playerA.getUsername() + " has " + playerA.getWinCounter() + " wins in this battle!");
        System.out.println(playerB.getUsername() + " has " + playerB.getWinCounter() + " wins in this battle!");
        System.out.println("##############################################");
        if (playerA.getWinCounter() > playerB.getWinCounter()) {
            userRepository.setUserWinsByUsername(playerAName, playerA.getWins() + 1);
            userRepository.setUserLossesByUsername(playerBName, playerB.getLosses() + 1);
            userRepository.updateUserEloByUsername(playerA.getUsername(), playerA.getElo() + 3);
            userRepository.updateUserEloByUsername(playerB.getUsername(), playerB.getElo() - 5);
            return new ResponseModel(playerA.getUsername() + " has won the game!", 200);
        } else if (playerA.getWinCounter() < playerB.getWinCounter()) {
            userRepository.setUserLossesByUsername(playerAName, playerA.getLosses() + 1);
            userRepository.setUserWinsByUsername(playerBName, playerB.getWins() + 1);
            userRepository.updateUserEloByUsername(playerA.getUsername(), playerA.getElo() - 5);
            userRepository.updateUserEloByUsername(playerB.getUsername(), playerB.getElo() + 3);
            return new ResponseModel(playerB.getUsername() + " has won the game!", 200);
        } else {
            return new ResponseModel("Draw!", 200);
        }
    }
}
