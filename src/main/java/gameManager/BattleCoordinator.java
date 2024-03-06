package gameManager;

import gameElements.card.*;
import gameElements.user.User;

import java.util.List;

public class BattleCoordinator {
    private final User playerOne;
    private final User playerTwo;
    private final List<Card> playerOneCards;
    private final List<Card> playerTwoCards;
    private final BattleLogger battleLog;
    private final RoundWinnerDecider roundWinnerDecider;
    private final CardShuffler cardShuffler = new CardShuffler();

    public BattleCoordinator(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards, BattleLogger battleLog) {
        this.playerOne = playerOne;
        this.playerOneCards = playerOneCards;
        this.playerTwo = playerTwo;
        this.playerTwoCards = playerTwoCards;
        this.battleLog = battleLog;
        this.roundWinnerDecider = new RoundWinnerDecider(battleLog);
    }

    public synchronized void startBattle() {
        int roundCounter = 1;
        final int MAX_ROUNDS = 100;
        while (!playerOneCards.isEmpty() && !playerTwoCards.isEmpty() && roundCounter <= MAX_ROUNDS) {
            resetTemporaryDamage(playerOneCards);
            resetTemporaryDamage(playerTwoCards);

            battleLog.printRoundStart(roundCounter, Thread.currentThread());
            cardShuffler.shuffleCards(playerOneCards, playerTwoCards);
            roundCounter++;

            if(roundWinnerDecider.needToConsiderCardSpecialties(playerOne, playerOneCards, playerTwo, playerTwoCards))
                continue;

            roundWinnerDecider.handleCommonRoundFight(playerOne, playerOneCards, playerTwo, playerTwoCards);
        }
        battleLog.printBattleEnd();
    }

    private void resetTemporaryDamage(List<Card> cards) {
        for (Card card : cards) {
            card.setTmpDamage(card.getDamage());
        }
    }
}
