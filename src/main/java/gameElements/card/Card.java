package gameElements.card;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class Card {
    String id;
    private CardType name;
    private double damage;
    private double tmpDamage;
    private CardElement cardElement;

    public Card(String id, CardType name, int damage, CardElement cardElement) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.tmpDamage = damage;
        this.cardElement = cardElement;
    }
}
