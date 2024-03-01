package gameManager;

import gameElements.card.Card;

import java.util.Collections;
import java.util.List;

public class CardShuffler {
    @SafeVarargs
    public final void shuffleCards(List<Card>... decks) {
        for (List<Card> deck : decks) {
            Collections.shuffle(deck);
        }
    }
}
