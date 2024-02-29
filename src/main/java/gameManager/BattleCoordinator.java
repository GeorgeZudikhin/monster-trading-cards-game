package gameManager;

import gameElements.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class BattleCoordinator {
    private static final Semaphore battleLock = new Semaphore(1);

    public static boolean tryAcquireBattle() {
        return battleLock.tryAcquire();
    }

    public static void releaseBattle() {
        battleLock.release();
    }
    private final int firstCard = 0;
    private final User playerOne;
    private final User playerTwo;
    private final List<Card> playerOneCards;
    private final List<Card> playerTwoCards;

    public BattleCoordinator(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        this.playerOne = playerOne;
        this.playerOneCards = playerOneCards;
        this.playerTwo = playerTwo;
        this.playerTwoCards = playerTwoCards;
    }

    public synchronized void startGame() {
        int roundCounter = 1;
        final int MAX_ROUNDS = 100;
        while (!playerOneCards.isEmpty() && !playerTwoCards.isEmpty() && roundCounter <= MAX_ROUNDS) {
            System.out.println("********************************");
            System.out.println("Round #" + roundCounter + " Thread: " + Thread.currentThread().getName());
            shuffleCards(playerOneCards, playerTwoCards);
            determineRoundWinner(playerOne, playerOneCards, playerTwo, playerTwoCards);
            roundCounter++;
        }
        System.out.println("End!");
    }

    public synchronized void shuffleCards(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Collections.shuffle(playerOneCards);
        Collections.shuffle(playerTwoCards);
    }

    public synchronized void setNewWinnerAttributes(User winner, List<Card> winnerCards, List<Card> loserCards) {
        System.out.println(winner.getUsername() + " wins the round!");
        Card tmpCard = loserCards.get(firstCard);
        loserCards.remove(tmpCard);
        winnerCards.add(tmpCard);
        winner.setWinCounter(playerOne.getWinCounter() + 1);
    }

//    public synchronized void playerAWin(User playerOne, List<Card> playerOneCards, List<Card> playerTwoCards) {
//        System.out.println(playerOne.getUsername() + " wins the round!");
//        Card tmpCard = playerTwoCards.get(firstCard);
//        playerTwoCards.remove(tmpCard);
//        playerOneCards.add(tmpCard);
//        playerOne.setWinCounter(playerOne.getWinCounter() + 1);
//    }
//
//    public synchronized void playerBWin(User playerTwo, List<Card> playerTwoCards, List<Card> playerOneCards) {
//        System.out.println(playerTwo.getUsername() + " wins the round!");
//        Card tmpCard = playerOneCards.get(firstCard);
//        playerOneCards.remove(tmpCard);
//        playerTwoCards.add(tmpCard);
//        playerTwo.setWinCounter(playerTwo.getWinCounter() + 1);
//    }

    public synchronized void determineRoundWinner(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
        CardName firstCardName = playerOneCards.get(firstCard).getName();
        CardName secondCardName = playerTwoCards.get(firstCard).getName();
        ElementType firstCardElementType = playerOneCards.get(firstCard).getElementType();
        ElementType secondCardElementType = playerTwoCards.get(firstCard).getElementType();

        // Check specialties
        if (firstCardName == CardName.GOBLIN && secondCardName == CardName.DRAGON) {
            System.out.println("Goblins are too afraid of Dragons to attack");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.DRAGON && secondCardName == CardName.GOBLIN) {
            System.out.println("Goblins are too afraid of Dragons to attack");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.ORK && secondCardName == CardName.WIZZARD) {
            System.out.println("Wizzard can control Orks so they are not able to damage them");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.WIZZARD && secondCardName == CardName.ORK) {
            System.out.println("Wizzard can control Orks so they are not able to damage them");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.KNIGHT && secondCardName == CardName.SPELL && secondCardElementType == ElementType.WATER) {
            System.out.println("The armor of Knights is so heavy that WaterSpells make drown instantly");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.SPELL && firstCardElementType == ElementType.WATER && secondCardName == CardName.KNIGHT) {
            System.out.println("The armor of Knights is so heavy that WaterSpells make drown instantly");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.SPELL && secondCardName == CardName.KRAKE) {
            System.out.println("Krake is immune against spell");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerTwo, playerTwoCards, playerOneCards);
        } else if (firstCardName == CardName.KRAKE && secondCardName == CardName.SPELL) {
            System.out.println("Krake is immune against spell");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            setNewWinnerAttributes(playerOne, playerOneCards, playerTwoCards);
        } else if (firstCardName == CardName.ELF && firstCardElementType == ElementType.FIRE && secondCardName == CardName.DRAGON) {
            System.out.println("FireElves and Dragons are friends 4 ever");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
        } else if (firstCardName == CardName.DRAGON && secondCardName == CardName.ELF && secondCardElementType == ElementType.FIRE) {
            System.out.println("FireElves and Dragons are friends 4 ever");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
        } else if (playerOneCards.get(firstCard) instanceof SpellCard && playerTwoCards.get(firstCard) instanceof SpellCard) {
            System.out.println("Spell Fight");
            elementsCardFight(playerOneCards, playerTwoCards);
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            if (playerOneCards.get(firstCard).getTmpElementsDamage() > playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                setNewWinnerAttributes(playerOne, playerOneCards, playerTwoCards);
            } else if (playerOneCards.get(firstCard).getTmpElementsDamage() == playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                System.out.println("Draw!");
            } else {
                setNewWinnerAttributes(playerTwo, playerTwoCards, playerOneCards);
            }
        } else if (playerOneCards.get(firstCard) instanceof MonsterCard && playerTwoCards.get(firstCard) instanceof MonsterCard) {
            System.out.println("Monster Fight");
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            if (playerOneCards.get(firstCard).getDamage() > playerTwoCards.get(firstCard).getDamage()) {
                setNewWinnerAttributes(playerOne, playerOneCards, playerTwoCards);
            } else if (playerOneCards.get(firstCard).getDamage() < playerTwoCards.get(firstCard).getDamage()) {
                setNewWinnerAttributes(playerTwo, playerTwoCards, playerOneCards);
            } else {
                System.out.println("Draw!");
            }
        } else {
            System.out.println("Mixed Fight");
            elementsCardFight(playerOneCards, playerTwoCards);
            printLog(playerOne, playerOneCards, playerTwo, playerTwoCards);
            if (playerOneCards.get(firstCard).getTmpElementsDamage() > playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                setNewWinnerAttributes(playerOne, playerOneCards, playerTwoCards);
            } else if (playerOneCards.get(firstCard).getTmpElementsDamage() == playerTwoCards.get(firstCard).getTmpElementsDamage()) {
                System.out.println("Draw!");
            } else {
                setNewWinnerAttributes(playerTwo, playerTwoCards, playerOneCards);
            }
        }
    }

    public synchronized void playerABetterElement(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Card playerOneCard = playerOneCards.get(firstCard);
        Card playerTwoCard = playerTwoCards.get(firstCard);

        playerOneCard.setTmpElementsDamage(playerOneCard.getDamage() * 2);
        playerTwoCard.setTmpElementsDamage(playerTwoCard.getDamage() / 2);
    }

    public synchronized void playerBBetterElement(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Card playerOneCard = playerOneCards.get(firstCard);
        Card playerTwoCard = playerTwoCards.get(firstCard);

        playerTwoCard.setTmpElementsDamage(playerTwoCard.getDamage() * 2);
        playerOneCard.setTmpElementsDamage(playerOneCard.getDamage() / 2);
    }

    public synchronized void notEffectedFight(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Card playerOneCard = playerOneCards.get(firstCard);
        Card playerTwoCard = playerTwoCards.get(firstCard);

        playerTwoCard.setTmpElementsDamage(playerTwoCard.getDamage());
        playerOneCard.setTmpElementsDamage(playerOneCard.getDamage());
    }

    public synchronized void elementsCardFight(List<Card> playerACards, List<Card> playerBCards) {
        ElementType playerAElementType = playerACards.get(firstCard).getElementType();
        ElementType playerBElementType = playerBCards.get(firstCard).getElementType();

        if (playerAElementType == ElementType.FIRE && playerBElementType == ElementType.WATER) {
            playerBBetterElement(playerACards, playerBCards);
        } else if (playerBElementType == ElementType.FIRE && playerAElementType == ElementType.WATER) {
            playerABetterElement(playerACards, playerBCards);
        } else if (playerAElementType == ElementType.FIRE && playerBElementType == ElementType.NORMAL) {
            playerABetterElement(playerACards, playerBCards);
        } else if (playerBElementType == ElementType.FIRE && playerAElementType == ElementType.NORMAL) {
            playerBBetterElement(playerACards, playerBCards);
        } else if (playerAElementType == ElementType.NORMAL && playerBElementType == ElementType.WATER) {
            playerABetterElement(playerACards, playerBCards);
        } else if (playerBElementType == ElementType.NORMAL && playerAElementType == ElementType.WATER) {
            playerBBetterElement(playerACards, playerBCards);
        } else {
            notEffectedFight(playerACards, playerBCards);
        }
    }

    public synchronized void printLog(User playerOne, List<Card> playerOneCards, User playerTwo, List<Card> playerTwoCards) {
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
