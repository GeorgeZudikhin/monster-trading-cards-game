package gameManager;

import gameElements.card.*;
import gameElements.user.User;

import java.util.List;

public class RoundWinnerDecider {
    private final int firstCard = 0;
    private final BattleLogger battleLog;
    public RoundWinnerDecider(BattleLogger battleLog) {
        this.battleLog = battleLog;
    }
    public synchronized boolean needToConsiderCardSpecialties(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        CardType playerOneCardType = playerOneCards.get(firstCard).getType();
        CardType playerTwoCardType = playerTwoCards.get(firstCard).getType();
        CardElement firstCardCardElement = playerOneCards.get(firstCard).getCardElement();
        CardElement secondCardCardElement = playerTwoCards.get(firstCard).getCardElement();

        if (playerOneCardType == CardType.GOBLIN && playerTwoCardType == CardType.DRAGON) {
            battleLog.printDragonsBeatGoblins();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerTwo);
            transferCard(playerOneCards, playerTwoCards);
            return true;
        } else if (playerOneCardType == CardType.DRAGON && playerTwoCardType == CardType.GOBLIN) {
            battleLog.printDragonsBeatGoblins();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerOne);
            transferCard(playerTwoCards, playerOneCards);
            return true;
        } else if (playerOneCardType == CardType.ORK && playerTwoCardType == CardType.WIZARD) {
            battleLog.printWizardsBeatOrks();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerTwo);
            transferCard(playerOneCards, playerTwoCards);
            return true;
        } else if (playerOneCardType == CardType.WIZARD && playerTwoCardType == CardType.ORK) {
            battleLog.printWizardsBeatOrks();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerOne);
            transferCard(playerTwoCards, playerOneCards);
            return true;
        } else if (playerOneCardType == CardType.KNIGHT && playerTwoCardType == CardType.SPELL && secondCardCardElement == CardElement.WATER) {
            battleLog.printWaterSpellsBeatKnights();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerTwo);
            transferCard(playerOneCards, playerTwoCards);
            return true;
        } else if (playerOneCardType == CardType.SPELL && firstCardCardElement == CardElement.WATER && playerTwoCardType == CardType.KNIGHT) {
            battleLog.printWaterSpellsBeatKnights();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerOne);
            transferCard(playerTwoCards, playerOneCards);
            return true;
        } else if (playerOneCardType == CardType.SPELL && playerTwoCardType == CardType.KRAKEN) {
            battleLog.printKrakenBeatsSpells();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerTwo);
            transferCard(playerOneCards, playerTwoCards);
            return true;
        } else if (playerOneCardType == CardType.KRAKEN && playerTwoCardType == CardType.SPELL) {
            battleLog.printKrakenBeatsSpells();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerOne);
            transferCard(playerTwoCards, playerOneCards);
            return true;
        } else if (playerOneCardType == CardType.ELF && firstCardCardElement == CardElement.FIRE && playerTwoCardType == CardType.DRAGON) {
            battleLog.printFireElvesBeatDragons();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerOne);
            transferCard(playerTwoCards, playerOneCards);
            return true;
        } else if (playerOneCardType == CardType.DRAGON && playerTwoCardType == CardType.ELF && secondCardCardElement == CardElement.FIRE) {
            battleLog.printFireElvesBeatDragons();
            battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
            increaseWinnerCount(playerTwo);
            transferCard(playerOneCards, playerTwoCards);
            return true;
        }
        return false;
    }

    public synchronized void handleCommonRoundFight(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        if(!(playerOneCards.get(firstCard) instanceof MonsterCard && playerTwoCards.get(firstCard) instanceof MonsterCard))
            decideDamageBasedOnElement(playerOneCards, playerTwoCards);

        battleLog.printRoundOutcome(playerOne, playerOneCards, playerTwo, playerTwoCards);
        if (playerOneCards.get(firstCard).getTmpDamage() > playerTwoCards.get(firstCard).getTmpDamage()) {
            increaseWinnerCount(playerOne);
            transferCard(playerTwoCards, playerOneCards);
        } else if (playerTwoCards.get(firstCard).getTmpDamage() > playerOneCards.get(firstCard).getTmpDamage()) {
            increaseWinnerCount(playerTwo);
            transferCard(playerOneCards, playerTwoCards);
        } else {
            battleLog.printRoundResultDraw();
        }
    }

    public synchronized void decideDamageBasedOnElement(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Card playerOneCard = playerOneCards.get(firstCard);
        Card playerTwoCard = playerTwoCards.get(firstCard);
        CardElement playerOneCardElement = playerOneCard.getCardElement();
        CardElement playerTwoCardElement = playerTwoCard.getCardElement();

        if (playerOneCardElement == CardElement.NORMAL && playerTwoCardElement == CardElement.WATER) {
            considerElementSpecialties(playerTwoCard, playerOneCard);
        } else if (playerTwoCardElement == CardElement.NORMAL && playerOneCardElement == CardElement.WATER) {
            considerElementSpecialties(playerOneCard, playerTwoCard);
        } else if (playerOneCardElement == CardElement.FIRE && playerTwoCardElement == CardElement.WATER) {
            considerElementSpecialties(playerOneCard, playerTwoCard);
        } else if (playerTwoCardElement == CardElement.FIRE && playerOneCardElement == CardElement.WATER) {
            considerElementSpecialties(playerTwoCard, playerOneCard);
        } else if (playerOneCardElement == CardElement.FIRE && playerTwoCardElement == CardElement.NORMAL) {
            considerElementSpecialties(playerTwoCard, playerOneCard);
        } else if (playerTwoCardElement == CardElement.FIRE && playerOneCardElement == CardElement.NORMAL) {
            considerElementSpecialties(playerOneCard, playerTwoCard);
        }
    }

    public synchronized void considerElementSpecialties(Card loserCard, Card winnerCard) {
        loserCard.setTmpDamage(loserCard.getDamage() / 2);
        winnerCard.setTmpDamage(winnerCard.getDamage() * 2);
    }

    public synchronized void increaseWinnerCount(User winner) {
        battleLog.printRoundWinner(winner);
        winner.setRoundWins(winner.getRoundWins() + 1);
    }

    public synchronized void transferCard(List<Card> fromDeck, List<Card> toDeck) {
        Card card = fromDeck.remove(firstCard);
        toDeck.add(card);
    }
}
