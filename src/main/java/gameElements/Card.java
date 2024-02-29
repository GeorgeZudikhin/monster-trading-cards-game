package gameElements;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class Card {
    String id;
    private CardName name;
    private double damage;
    private double tmpElementsDamage;
    private ElementType elementType;

    public Card(String id, CardName name, int damage, ElementType elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }
}
