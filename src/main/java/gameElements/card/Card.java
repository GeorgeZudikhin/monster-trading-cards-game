package gameElements.card;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class Card {
    String id;
    private CardType type;
    private CardElement cardElement;
    private double damage;
    private double tmpDamage;

    public Card(String id, CardType type, CardElement cardElement, int damage) {
        this.id = id;
        this.type = type;
        this.damage = damage;
        this.tmpDamage = damage;
        this.cardElement = cardElement;
    }
}
