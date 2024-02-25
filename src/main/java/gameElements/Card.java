package gameElements;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
public abstract class Card {
    private CardName name;
    private double damage;
    private double tmpElementsDamage;
    private ElementType elementType;

    public Card(CardName name, int damage, ElementType elementType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    @Override
    public String toString() {
        return "Card: " +
                "name=" + name +
                ", damage=" + damage +
                ", elementType=" + elementType +
                '.';
    }

    public static void shuffleCards(List<Card> playerACards, List<Card> playerBCards) {
        Collections.shuffle(playerACards);
        Collections.shuffle(playerBCards);
    }

    public static void playerABetterElement(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        Card playerACard = playerACards.get(x);
        Card playerBCard = playerBCards.get(y);

        playerACard.setTmpElementsDamage(playerACard.getDamage() * 2);
        playerBCard.setTmpElementsDamage(playerBCard.getDamage() / 2);
    }

    public static void playerBBetterElement(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        Card playerACard = playerACards.get(x);
        Card playerBCard = playerBCards.get(y);

        playerBCard.setTmpElementsDamage(playerBCard.getDamage() * 2);
        playerACard.setTmpElementsDamage(playerACard.getDamage() / 2);
    }

    public static void notEffectedFight(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        Card playerACard = playerACards.get(x);
        Card playerBCard = playerBCards.get(y);

        playerBCard.setTmpElementsDamage(playerBCard.getDamage());
        playerACard.setTmpElementsDamage(playerACard.getDamage());
    }


    public static void elementsCardFight(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        ElementType playerAElementType = playerACards.get(x).getElementType();
        ElementType playerBElementType = playerBCards.get(y).getElementType();

        // FIRE vs WATER
        if (playerAElementType == ElementType.FIRE && playerBElementType == ElementType.WATER) {
            playerBBetterElement(playerACards, playerBCards, x, y);
        } else if (playerBElementType == ElementType.FIRE && playerAElementType == ElementType.WATER) {
            playerABetterElement(playerACards, playerBCards, x, y);
        // FIRE vs NORMAL
        } else if (playerAElementType == ElementType.FIRE && playerBElementType == ElementType.NORMAL) {
            playerABetterElement(playerACards, playerBCards, x, y);
        } else if (playerBElementType == ElementType.FIRE && playerAElementType == ElementType.NORMAL) {
            playerBBetterElement(playerACards, playerBCards, x, y);
        // NORMAL vs WATER
        } else if (playerAElementType == ElementType.NORMAL && playerBElementType == ElementType.WATER) {
            playerABetterElement(playerACards, playerBCards, x, y);
        } else if (playerBElementType == ElementType.NORMAL && playerAElementType == ElementType.WATER) {
            playerBBetterElement(playerACards, playerBCards, x, y);
        // ALL OTHER FIGHTS
        } else {
            notEffectedFight(playerACards, playerBCards, x, y);
        }
    }

}
