package gameManager;

import gameElements.Card;
import gameElements.User;
import http.response.ResponseModel;
import repository.CardRepository;
import repository.UserRepository;

import java.util.List;

public class BattleInitiator {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public BattleInitiator(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public ResponseModel initializeBattle(String playerOneUsername, String playerTwoUsername) {
        User playerOne = new User(playerOneUsername);
        User playerTwo = new User(playerTwoUsername);

        List<Card> playerOneCards = cardRepository.getUserDeckByUserID(userRepository.returnUserIDFromUsername(playerOne.getUsername()));
        List<Card> playerTwoCards = cardRepository.getUserDeckByUserID(userRepository.returnUserIDFromUsername(playerTwo.getUsername()));

        initializePlayers(playerOne, playerOneCards, playerTwo, playerTwoCards);

        BattleCoordinator.startGame(playerOne, playerOneCards, playerTwo, playerTwoCards);
        System.out.println("##############################################");
        System.out.println(playerOne.getUsername() + " has " + playerOne.getWinCounter() + " wins in this battle!");
        System.out.println(playerTwo.getUsername() + " has " + playerTwo.getWinCounter() + " wins in this battle!");
        System.out.println("##############################################");
        if (playerOne.getWinCounter() > playerTwo.getWinCounter()) {
            userRepository.setUserWinsByUsername(playerOneUsername, playerOne.getWins() + 1);
            userRepository.setUserLossesByUsername(playerTwoUsername, playerTwo.getLosses() + 1);
            userRepository.updateUserEloByUsername(playerOne.getUsername(), playerOne.getElo() + 3);
            userRepository.updateUserEloByUsername(playerTwo.getUsername(), playerTwo.getElo() - 5);
            return new ResponseModel(playerOne.getUsername() + " has won the game!", 200);
        } else if (playerOne.getWinCounter() < playerTwo.getWinCounter()) {
            userRepository.setUserLossesByUsername(playerOneUsername, playerOne.getLosses() + 1);
            userRepository.setUserWinsByUsername(playerTwoUsername, playerTwo.getWins() + 1);
            userRepository.updateUserEloByUsername(playerOne.getUsername(), playerOne.getElo() - 5);
            userRepository.updateUserEloByUsername(playerTwo.getUsername(), playerTwo.getElo() + 3);
            return new ResponseModel(playerTwo.getUsername() + " has won the game!", 200);
        } else {
            return new ResponseModel("Draw!", 200);
        }
    }

    private void initializePlayers(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        playerOne.setCoins(userRepository.returnUserCoins(playerOne.getUsername()));
        playerOne.setElo(userRepository.getUserEloByUsername(playerOne.getUsername()));
        playerOne.setWins(userRepository.getUserWinsByUsername(playerOne.getUsername()));
        playerOne.setLosses(userRepository.getUserLossesByUsername(playerOne.getUsername()));
        playerOne.setDeck(playerOneCards);

        playerTwo.setCoins(userRepository.returnUserCoins(playerTwo.getUsername()));
        playerTwo.setElo(userRepository.getUserEloByUsername(playerTwo.getUsername()));
        playerTwo.setWins(userRepository.getUserWinsByUsername(playerTwo.getUsername()));
        playerTwo.setLosses(userRepository.getUserLossesByUsername(playerTwo.getUsername()));
        playerTwo.setDeck(playerTwoCards);
    }
}
