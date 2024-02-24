package businessLogic;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User {
    private String username;
    private String password;
    private int coins;
    private int elo;
    private int wins; // the total number of battles or games a user has won over time, not just within a single session
    private int winCounter; // track the number of individual rounds won by a user within a single game session
    private int losses;
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
