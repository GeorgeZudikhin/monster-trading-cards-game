package gameManager;

import gameElements.card.Card;
import gameElements.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BattleLogger {
    protected static final Logger logger = LogManager.getLogger();
    public void printRoundStart(int roundCounter, Thread currentThread) {
        logger.info("-------------------------------");
        logger.info("Round №" + roundCounter + " in Thread: " + currentThread.getName());
    }

    public void printBattleEnd() {
        logger.info("End!");
    }

    public void printRoundWinner(User winner) {
        logger.info(winner.getUsername() + " wins the round!");
    }

    public void printAmountOfRoundsWon(User player) {
        logger.info(player.getUsername() + " has won " + player.getRoundWins() + " round in this battle!");
    }

    public void printBattleWinner(User winner) {
        logger.info(winner.getUsername() + " has won the game!");
    }
    public void printDragonsBeatGoblins() { logger.info("Goblins are too afraid of Dragons to attack"); }
    public void printWizardsBeatOrks() { logger.info("Wizard can control Orks so they are not able to damage them"); }
    public void printWaterSpellsBeatKnights() { logger.info("The armor of Knights is so heavy that WaterSpells make them drown them instantly"); }
    public void printKrakenBeatsSpells() { logger.info("The Kraken is immune against spells"); }
    public void printFireElvesBeatDragons() { logger.info("The FireElves know Dragons since they were little and can evade their attacks"); }

    public void printRoundOutcome(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        int firstCard = 0;
        logger.info(playerOne.getUsername() + " with " + playerOneCards.get(firstCard).getCardElement() + playerOneCards.get(firstCard).getName()
                + "[DMG=" + playerOneCards.get(firstCard).getDamage() + "] VS " + playerTwo.getUsername() + " with "
                + playerTwoCards.get(firstCard).getCardElement() + playerTwoCards.get(firstCard).getName()
                + "[DMG=" + playerTwoCards.get(firstCard).getDamage() + "]" + " --DMG AFTER SPECIALTIES--> "
                + playerOneCards.get(firstCard).getTmpDamage() + " VS " + playerTwoCards.get(firstCard).getTmpDamage());
    }

    public void printBattleResultDraw() {
        logger.info("The game has ended in draw!");
    }

    public void printRoundResultDraw() {
        logger.info("Draw!");
    }
}
