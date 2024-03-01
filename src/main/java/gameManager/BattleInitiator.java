package gameManager;

import gameElements.card.Card;
import gameElements.user.User;
import http.response.ResponseModel;
import repository.BattleRepository;
import repository.CardRepository;
import repository.UserRepository;

import java.util.List;

public class BattleInitiator {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BattleRepository battleRepository;
    private final BattleLogger battleLog = new BattleLogger();

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
        BattleCoordinator battleCoordinator = new BattleCoordinator(playerOne, playerOneCards, playerTwo, playerTwoCards, battleLog);

        battleCoordinator.startBattle();

        battleLog.printAmountOfRoundsWon(playerOne);
        battleLog.printAmountOfRoundsWon(playerTwo);

        battleRepository.resetUserReadyStatus();

        if (playerOne.getRoundWins() > playerTwo.getRoundWins()) {
            updateUserStatsAfterBattle(playerOne, playerTwo);
            battleLog.printBattleWinner(playerOne);
            return new ResponseModel(playerOne.getUsername() + " has won the game!", 200);
        } else if (playerTwo.getRoundWins() > playerOne.getRoundWins()) {
            updateUserStatsAfterBattle(playerTwo, playerOne);
            battleLog.printBattleWinner(playerTwo);
            return new ResponseModel(playerTwo.getUsername() + " has won the game!", 200);
        } else {
            battleLog.printBattleResultDraw();
            return new ResponseModel("Draw!", 200);
        }
    }

    private void initializePlayer(User player, List<Card> playerCards) {
        player.setCoins(userRepository.returnUserCoins(player.getUsername()));
        player.setElo(userRepository.getUserEloByUsername(player.getUsername()));
        player.setBattleWins(userRepository.getUserWinsByUsername(player.getUsername()));
        player.setBattleLosses(userRepository.getUserLossesByUsername(player.getUsername()));
        player.setDeck(playerCards);
    }

    private void updateUserStatsAfterBattle(User winner, User loser) {
        userRepository.setUserWinsByUsername(winner.getUsername(), winner.getBattleWins() + 1);
        userRepository.setUserLossesByUsername(loser.getUsername(), loser.getBattleLosses() + 1);
        userRepository.updateUserEloByUsername(winner.getUsername(), winner.getElo() + 3);
        userRepository.updateUserEloByUsername(loser.getUsername(), loser.getElo() - 5);
    }
}
