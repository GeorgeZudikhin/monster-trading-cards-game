package businessLogic;

import java.util.List;

public class BattleManager {

    private static final int MAX_ROUNDS = 100;

    public static void startGame(List<Card> playerACards, List<Card> playerBCards, User playerA, User playerB) {

        int x = 0;
        int y = 0;

        for (int roundCounter = 1; roundCounter <= MAX_ROUNDS; roundCounter++) {
            if (playerACards.isEmpty() || playerBCards.isEmpty()) {
                System.out.println("________________________________");
                System.out.println("End of game ");
                break;
            } else if (roundCounter == MAX_ROUNDS) {
                System.out.println("End result: Draw");
                break;
            }
            System.out.println("________________________________");
            System.out.println("Round" + roundCounter);
            Card.shuffleCards(playerACards, playerBCards);
            checkWinner(playerACards, playerBCards, playerA, playerB, x, y);
        }
    }

    public static void playerAWin(List<Card> playerACards, List<Card> playerBCards, User playerA, int y) {
        System.out.println(playerA.getUsername() + " wins ");
        Card tmpCard = playerBCards.get(y);
        playerBCards.remove(tmpCard);
        playerACards.add(tmpCard);
        playerA.setWinCounter(playerA.getWinCounter() + 1);
    }

    public static void playerBWin(List<Card> playerACards, List<Card> playerBCards, User playerB, int x) {
        System.out.println(playerB.getUsername() + " wins ");
        Card tmpCard = playerACards.get(x);
        playerACards.remove(tmpCard);
        playerBCards.add(tmpCard);
        playerB.setWinCounter(playerB.getWinCounter() + 1);
    }

    public static void checkWinner(List<Card> playerACards, List<Card> playerBCards, User playerA, User playerB, int x, int y) {
        CardName firstCardName = playerACards.get(x).getName();
        CardName secondCardName = playerBCards.get(y).getName();
        Element firstCardElement = playerACards.get(x).getElementType();
        Element secondCardElement = playerBCards.get(y).getElementType();

        // Check specialties
        if (firstCardName == CardName.GOBLIN && secondCardName == CardName.DRAGON) {
            System.out.println("Goblins are too afraid of Dragons to attack");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerBWin(playerACards, playerBCards, playerB, x);
        } else if (firstCardName == CardName.DRAGON && secondCardName == CardName.GOBLIN) {
            System.out.println("Goblins are too afraid of Dragons to attack");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerAWin(playerACards, playerBCards, playerA, y);
        } else if (firstCardName == CardName.ORK && secondCardName == CardName.WIZZARD) {
            System.out.println("Wizzard can control Orks so they are not able to damage them");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerBWin(playerACards, playerBCards, playerB, x);
        } else if (firstCardName == CardName.WIZZARD && secondCardName == CardName.ORK) {
            System.out.println("Wizzard can control Orks so they are not able to damage them");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerAWin(playerACards, playerBCards, playerA, y);
        } else if (firstCardName == CardName.KNIGHT && secondCardName == CardName.SPELL && secondCardElement == Element.WATER) {
            System.out.println("The armor of Knights is so heavy that WaterSpells make drown instantly");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerBWin(playerACards, playerBCards, playerB, x);
        } else if (firstCardName == CardName.SPELL && firstCardElement == Element.WATER && secondCardName == CardName.KNIGHT) {
            System.out.println("The armor of Knights is so heavy that WaterSpells make drown instantly");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerAWin(playerACards, playerBCards, playerA, y);
        } else if (firstCardName == CardName.SPELL && secondCardName == CardName.KRAKE) {
            System.out.println("Krake is immune against spell");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerBWin(playerACards, playerBCards, playerB, x);
        } else if (firstCardName == CardName.KRAKE && secondCardName == CardName.SPELL) {
            System.out.println("Krake is immune against spell");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            playerAWin(playerACards, playerBCards, playerA, y);
        } else if (firstCardName == CardName.ELF && firstCardElement == Element.FIRE && secondCardName == CardName.DRAGON) {
            System.out.println("FireElves and Dragons are friends 4 ever");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
        } else if (firstCardName == CardName.DRAGON && secondCardName == CardName.ELF && secondCardElement == Element.FIRE) {
            System.out.println("FireElves and Dragons are friends 4 ever");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
        } else if (playerACards.get(x) instanceof SpellCard && playerBCards.get(y) instanceof SpellCard) { //SpellFights are effected by elements
            System.out.println("Spell Fight");
            Card.elementsCardFight(playerACards, playerBCards, x, y);
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            if (playerACards.get(x).getTmpElementsDamage() > playerBCards.get(y).getTmpElementsDamage()) {
                playerAWin(playerACards, playerBCards, playerA, y);
            } else if (playerACards.get(x).getTmpElementsDamage() == playerBCards.get(y).getTmpElementsDamage()) {
                System.out.println("draw");
            } else {
                playerBWin(playerACards, playerBCards, playerB, x);
            }
        } else if (playerACards.get(x) instanceof MonsterCard && playerBCards.get(y) instanceof MonsterCard) {
            System.out.println("Monster Fight");
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            if (playerACards.get(x).getDamage() > playerBCards.get(y).getDamage()) {
                playerAWin(playerACards, playerBCards, playerA, y);
            } else if (playerACards.get(x).getDamage() < playerBCards.get(y).getDamage()) {
                playerBWin(playerACards, playerBCards, playerB, x);
            } else {
                System.out.println("draw");
            }
        } else {
            System.out.println("Mixed Fight"); //Elements effect also mixed fights like spellfight
            Card.elementsCardFight(playerACards, playerBCards, x, y);
            printLog(playerACards, playerBCards, playerA, playerB, x, y);
            if (playerACards.get(x).getTmpElementsDamage() > playerBCards.get(y).getTmpElementsDamage()) {
                playerAWin(playerACards, playerBCards, playerA, y);
            } else if (playerACards.get(x).getTmpElementsDamage() == playerBCards.get(y).getTmpElementsDamage()) {
                System.out.println("draw");
            } else {
                playerBWin(playerACards, playerBCards, playerB, x);
            }
        }
    }

    static void printLog(List<Card> playerACards, List<Card> playerBCards, User playerA, User playerB, int x, int y) {
        if (playerACards.get(x) instanceof SpellCard && playerBCards.get(y) instanceof SpellCard   ||
            playerACards.get(x) instanceof MonsterCard && playerBCards.get(y) instanceof SpellCard ||
            playerACards.get(x) instanceof SpellCard && playerBCards.get(y) instanceof MonsterCard) {
            System.out.println(playerA.getUsername() +
                    ": "
                    + playerACards.get(x).getElementType() + playerACards.get(x).getName()
                    + "(" + playerACards.get(x).getDamage() + ")"
                    + " vs "
                    + playerB.getUsername()
                    + ": "
                    + playerBCards.get(y).getElementType() + playerBCards.get(y).getName()
                    + "(" + playerBCards.get(y).getDamage() + ")"
                    + " => "
                    + playerACards.get(x).getDamage() + " vs " + playerBCards.get(y).getDamage() + " => "
                    + playerACards.get(x).getTmpElementsDamage() + " vs " + playerBCards.get(y).getTmpElementsDamage());

        } else {
            System.out.println(playerA.getUsername() +
                    ": "
                    + playerACards.get(x).getElementType() + playerACards.get(x).getName()
                    + "(" + playerACards.get(x).getDamage() + ")"
                    + " vs "
                    + playerB.getUsername()
                    + ": "
                    + playerBCards.get(y).getElementType() + playerBCards.get(y).getName()
                    + "(" + playerBCards.get(y).getDamage() + ")"
                    + " => "
                    + playerACards.get(x).getDamage() + " vs " + playerBCards.get(y).getDamage());
        }
    }
}
