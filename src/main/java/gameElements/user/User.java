package gameElements.user;

import gameElements.card.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User {
    private String username;
    private int coins;
    private int elo;
    private int roundWins;
    private int battleWins;
    private int battleLosses;
    private List<Card> stack;
    private List<Card> deck;

    public User(String username) {
        this.username = username;
        this.coins = 20;
        this.elo = 100;
        this.stack = new ArrayList<>();
        this.deck = new ArrayList<>();
    }
}
