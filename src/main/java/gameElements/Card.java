package gameElements;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
public abstract class Card {
    String id;
    private CardName name;
    private double damage;
    private double tmpElementsDamage;
    private ElementType elementType;

    private static final int firstCard = 0;

    public Card(String id, CardName name, int damage, ElementType elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    @Override
    public String toString() {
        return "Card: " +
                "id=" + id +
                ", name=" + name +
                ", damage=" + damage +
                ", elementType=" + elementType +
                '.';
    }

    public static void shuffleCards(List<Card> playerACards, List<Card> playerBCards) {
        Collections.shuffle(playerACards);
        Collections.shuffle(playerBCards);
    }

    public static void playerABetterElement(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Card playerOneCard = playerOneCards.get(firstCard);
        Card playerTwoCard = playerTwoCards.get(firstCard);

        playerOneCard.setTmpElementsDamage(playerOneCard.getDamage() * 2);
        playerTwoCard.setTmpElementsDamage(playerTwoCard.getDamage() / 2);
    }

    public static void playerBBetterElement(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Card playerOneCard = playerOneCards.get(firstCard);
        Card playerTwoCard = playerTwoCards.get(firstCard);

        playerTwoCard.setTmpElementsDamage(playerTwoCard.getDamage() * 2);
        playerOneCard.setTmpElementsDamage(playerOneCard.getDamage() / 2);
    }

    public static void notEffectedFight(List<Card> playerOneCards, List<Card> playerTwoCards) {
        Card playerOneCard = playerOneCards.get(firstCard);
        Card playerTwoCard = playerTwoCards.get(firstCard);

        playerTwoCard.setTmpElementsDamage(playerTwoCard.getDamage());
        playerOneCard.setTmpElementsDamage(playerOneCard.getDamage());
    }


    public static void elementsCardFight(List<Card> playerACards, List<Card> playerBCards) {
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

}
