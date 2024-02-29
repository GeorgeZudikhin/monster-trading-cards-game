package gameManager;

import gameElements.Card;
import gameElements.User;
import http.response.ResponseModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.BattleRepository;
import repository.CardRepository;
import repository.UserRepository;

import java.util.List;

public class BattleInitiator {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BattleRepository battleRepository;
    protected static final Logger logger = LogManager.getLogger();

    public BattleInitiator(UserRepository userRepository, CardRepository cardRepository, BattleRepository battleRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.battleRepository = battleRepository;
    }

    public ResponseModel initializeBattle(String playerOneUsername, String playerTwoUsername) {
        User playerOne = new User(playerOneUsername);
        User playerTwo = new User(playerTwoUsername);

        List<Card> playerOneCards = cardRepository.getUserDeckByUserID(userRepository.returnUserIDFromUsername(playerOne.getUsername()));
        List<Card> playerTwoCards = cardRepository.getUserDeckByUserID(userRepository.returnUserIDFromUsername(playerTwo.getUsername()));

        initializePlayer(playerOne, playerOneCards);
        initializePlayer(playerTwo, playerTwoCards);
        BattleCoordinator battleCoordinator = new BattleCoordinator(playerOne, playerOneCards, playerTwo, playerTwoCards);

        battleCoordinator.startGame();
        logger.info("##############################################");
        logger.info(playerOne.getUsername() + " has won " + playerOne.getWinCounter() + " round in this battle!");
        logger.info(playerTwo.getUsername() + " has won " + playerTwo.getWinCounter() + " round in this battle!");
        logger.info("##############################################");
        battleRepository.resetUserReadyStatus();
        if (playerOne.getWinCounter() > playerTwo.getWinCounter()) {
            userRepository.setUserWinsByUsername(playerOneUsername, playerOne.getWins() + 1);
            userRepository.setUserLossesByUsername(playerTwoUsername, playerTwo.getLosses() + 1);
            userRepository.updateUserEloByUsername(playerOne.getUsername(), playerOne.getElo() + 3);
            userRepository.updateUserEloByUsername(playerTwo.getUsername(), playerTwo.getElo() - 5);
            System.out.println(playerOne.getUsername() + " has won the game!");
            return new ResponseModel(playerOne.getUsername() + " has won the game!", 200);
        } else if (playerOne.getWinCounter() < playerTwo.getWinCounter()) {
            userRepository.setUserLossesByUsername(playerOneUsername, playerOne.getLosses() + 1);
            userRepository.setUserWinsByUsername(playerTwoUsername, playerTwo.getWins() + 1);
            userRepository.updateUserEloByUsername(playerOne.getUsername(), playerOne.getElo() - 5);
            userRepository.updateUserEloByUsername(playerTwo.getUsername(), playerTwo.getElo() + 3);
            System.out.println(playerTwo.getUsername() + " has won the game!");
            return new ResponseModel(playerTwo.getUsername() + " has won the game!", 200);
        } else {
            System.out.println("The game has ended in draw!");
            return new ResponseModel("Draw!", 200);
        }
    }

    private void initializePlayer(User player, List<Card> playerCards) {
        player.setCoins(userRepository.returnUserCoins(player.getUsername()));
        player.setElo(userRepository.getUserEloByUsername(player.getUsername()));
        player.setWins(userRepository.getUserWinsByUsername(player.getUsername()));
        player.setLosses(userRepository.getUserLossesByUsername(player.getUsername()));
        player.setDeck(playerCards);
    }
}
