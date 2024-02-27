package gameManager;

import gameElements.*;

import java.util.List;

public class BattleCoordinator {

    private static final int MAX_ROUNDS = 100;
    private static final int firstCard = 0;

    public static void startGame(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        for (int roundCounter = 1; roundCounter <= MAX_ROUNDS; roundCounter++) {
            if (playerOneCards.isEmpty() || playerTwoCards.isEmpty()) {
                System.out.println("#################################");
                System.out.println("End!");
                break;
            } else if (roundCounter == MAX_ROUNDS) {
                System.out.println("End result: Draw");
                break;
            }
            System.out.println("********************************");
            System.out.println("Round #" + roundCounter);
            Card.shuffleCards(playerOneCards, playerTwoCards);
            checkWinner(playerOne, playerOneCards, playerTwo, playerTwoCards);
        }
    }

    public static void playerAWin(User playerOne, List<Card> playerOneCards, List<Card> playerTwoCards) {
        System.out.println(playerOne.getUsername() + " wins the round!");
        Card tmpCard = playerTwoCards.get(firstCard);
        playerTwoCards.remove(tmpCard);
        playerOneCards.add(tmpCard);
        playerOne.setWinCounter(playerOne.getWinCounter() + 1);
    }

    public static void playerBWin(User playerTwo, List<Card> playerTwoCards, List<Card> playerOneCards) {
        System.out.println(playerTwo.getUsername() + " wins the round!");
        Card tmpCard = playerOneCards.get(firstCard);
        playerOneCards.remove(tmpCard);
        playerTwoCards.add(tmpCard);
        playerTwo.setWinCounter(playerTwo.getWinCounter() + 1);
    }

    public static void checkWinner(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        CardName firstCardName = playerOneCards.get(firstCard).getName();
        CardName secondCardName = playerTwoCards.get(firstCard).getName();
        ElementType firstCardElementType = playerOneCards.get(firstCard).getElementType();
        ElementType secondCardElementType = playerTwoCards.get(firstCard).getElementType();

        // Check specialties
        if (firstCardName == CardName.GOBLIN && secondCardName == CardName.DRAGON) {
            System.out.println("Goblins are too afraid of Dragons to attack");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerBWin(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.DRAGON && secondCardName == CardName.GOBLIN) {
            System.out.println("Goblins are too afraid of Dragons to attack");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerAWin(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.ORK && secondCardName == CardName.WIZZARD) {
            System.out.println("Wizzard can control Orks so they are not able to damage them");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerBWin(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.WIZZARD && secondCardName == CardName.ORK) {
            System.out.println("Wizzard can control Orks so they are not able to damage them");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerAWin(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.KNIGHT && secondCardName == CardName.SPELL && secondCardElementType == ElementType.WATER) {
            System.out.println("The armor of Knights is so heavy that WaterSpells make drown instantly");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerBWin(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.SPELL && firstCardElementType == ElementType.WATER && secondCardName == CardName.KNIGHT) {
            System.out.println("The armor of Knights is so heavy that WaterSpells make drown instantly");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerAWin(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.SPELL && secondCardName == CardName.KRAKE) {
            System.out.println("Krake is immune against spell");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerBWin(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.KRAKE && secondCardName == CardName.SPELL) {
            System.out.println("Krake is immune against spell");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            playerAWin(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.ELF && firstCardElementType == ElementType.FIRE && secondCardName == CardName.DRAGON) {
            System.out.println("FireElves and Dragons are friends 4 ever");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
        } else if (firstCardName == CardName.DRAGON && secondCardName == CardName.ELF && secondCardElementType == ElementType.FIRE) {
            System.out.println("FireElves and Dragons are friends 4 ever");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
        } else if (playerOneCards.get(firstCard) instanceof SpellCard && playerTwoCards.get(firstCard) instanceof SpellCard) { //SpellFights are effected by elements
            System.out.println("Spell Fight");
            Card.elementsCardFight(playerOneCards, playerTwoCards);
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            if (playerOneCards.get(firstCard).getTmpElementsDamage() > playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                playerAWin(playerOne, playerOneCards, playerTwoCards);
            } else if (playerOneCards.get(firstCard).getTmpElementsDamage() == playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                System.out.println("draw");
            } else {
                playerBWin(playerTwo, playerTwoCards, playerOneCards);
            }
        } else if (playerOneCards.get(firstCard) instanceof MonsterCard && playerTwoCards.get(firstCard) instanceof MonsterCard) {
            System.out.println("Monster Fight");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            if (playerOneCards.get(firstCard).getDamage() > playerTwoCards.get(firstCard).getDamage()) {
                playerAWin(playerOne, playerOneCards, playerTwoCards);
            } else if (playerOneCards.get(firstCard).getDamage() < playerTwoCards.get(firstCard).getDamage()) {
                playerBWin(playerTwo, playerTwoCards, playerOneCards);
            } else {
                System.out.println("draw");
            }
        } else {
            System.out.println("Mixed Fight"); //Elements effect also mixed fights like spellfight
            Card.elementsCardFight(playerOneCards, playerTwoCards);
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            if (playerOneCards.get(firstCard).getTmpElementsDamage() > playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                playerAWin(playerOne, playerOneCards, playerTwoCards);
            } else if (playerOneCards.get(firstCard).getTmpElementsDamage() == playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                System.out.println("draw");
            } else {
                playerBWin(playerTwo, playerTwoCards, playerOneCards);
            }
        }
    }

    static void printLog(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        if (playerOneCards.get(firstCard) instanceof SpellCard && playerTwoCards.get(firstCard) instanceof SpellCard   ||
            playerOneCards.get(firstCard) instanceof MonsterCard && playerTwoCards.get(firstCard) instanceof SpellCard ||
            playerOneCards.get(firstCard) instanceof SpellCard && playerTwoCards.get(firstCard) instanceof MonsterCard) {
            System.out.println(playerOne.getUsername() +
                    ": "
                    + playerOneCards.get(firstCard).getElementType() + playerOneCards.get(firstCard).getName()
                    + "(" + playerOneCards.get(firstCard).getDamage() + ")"
                    + " vs "
                    + playerTwo.getUsername()
                    + ": "
                    + playerTwoCards.get(firstCard).getElementType() + playerTwoCards.get(firstCard).getName()
                    + "(" + playerTwoCards.get(firstCard).getDamage() + ")"
                    + " => "
                    + playerOneCards.get(firstCard).getDamage() + " vs " + playerTwoCards.get(firstCard).getDamage() + " => "
                    + playerOneCards.get(firstCard).getTmpElementsDamage() + " vs " + playerTwoCards.get(firstCard).getTmpElementsDamage());

        } else {
            System.out.println(playerOne.getUsername() +
                    ": "
                    + playerOneCards.get(firstCard).getElementType() + playerOneCards.get(firstCard).getName()
                    + "(" + playerOneCards.get(firstCard).getDamage() + ")"
                    + " vs "
                    + playerTwo.getUsername()
                    + ": "
                    + playerTwoCards.get(firstCard).getElementType() + playerTwoCards.get(firstCard).getName()
                    + "(" + playerTwoCards.get(firstCard).getDamage() + ")"
                    + " => "
                    + playerOneCards.get(firstCard).getDamage() + " vs " + playerTwoCards.get(firstCard).getDamage());
        }
    }
}
