package mtcg;

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
    private Element elementType;

    public Card(CardName name, int damage, Element elementType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    static void shuffleCards(List<Card> playerACards, List<Card> playerBCards) {
        Collections.shuffle(playerACards);
        Collections.shuffle(playerBCards);
    }

    static void playerABetterElement(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        Card playerACard = playerACards.get(x);
        Card playerBCard = playerBCards.get(y);

        playerACard.setTmpElementsDamage(playerACard.getDamage() * 2);
        playerBCard.setTmpElementsDamage(playerBCard.getDamage() / 2);
    }

    static void playerBBetterElement(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        Card playerACard = playerACards.get(x);
        Card playerBCard = playerBCards.get(y);

        playerBCard.setTmpElementsDamage(playerBCard.getDamage() * 2);
        playerACard.setTmpElementsDamage(playerACard.getDamage() / 2);
    }

    static void notEffectedFight(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        Card playerACard = playerACards.get(x);
        Card playerBCard = playerBCards.get(y);

        playerBCard.setTmpElementsDamage(playerBCard.getDamage());
        playerACard.setTmpElementsDamage(playerACard.getDamage());
    }


    static void elementsCardFight(List<Card> playerACards, List<Card> playerBCards, int x, int y) {
        Element playerAElement = playerACards.get(x).getElementType();
        Element playerBElement = playerBCards.get(y).getElementType();

        // FIRE vs WATER
        if (playerAElement == Element.FIRE && playerBElement == Element.WATER) {
            playerBBetterElement(playerACards, playerBCards, x, y);
        } else if (playerBElement == Element.FIRE && playerAElement == Element.WATER) {
            playerABetterElement(playerACards, playerBCards, x, y);
        // FIRE vs NORMAL
        } else if (playerAElement == Element.FIRE && playerBElement == Element.NORMAL) {
            playerABetterElement(playerACards, playerBCards, x, y);
        } else if (playerBElement == Element.FIRE && playerAElement == Element.NORMAL) {
            playerBBetterElement(playerACards, playerBCards, x, y);
        // NORMAL vs WATER
        } else if (playerAElement == Element.NORMAL && playerBElement == Element.WATER) {
            playerABetterElement(playerACards, playerBCards, x, y);
        } else if (playerBElement == Element.NORMAL && playerAElement == Element.WATER) {
            playerBBetterElement(playerACards, playerBCards, x, y);
        // ALL OTHER FIGHTS
        } else {
            notEffectedFight(playerACards, playerBCards, x, y);
        }
    }
}
