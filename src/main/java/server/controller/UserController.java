package server.controller;

import mtcg.*;
import database.DataBase;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    DataBase myData = new DataBase();

    public String regUser(String username, String password) {
        int userExists;
        userExists = myData.createUser(username, password);

        if (userExists == 1)
            return "Successfully registered";
        else
            return "User already exists";
    }

    public String logUser(String username, String password) {
        boolean authorised = myData.loginUser(username, password);
        if (authorised) {
            System.out.println("login success for user: " + username);
            return "Successfully loged in";
        } else {
            System.out.println("login failed for user: " + username);
            return "wrong password or username";
        }
    }

    public String returnWinLossRatio(String token) {
        float wins;
        float losses;
        String username = myData.returnUsernameFromToken(token);

        if (username == null || username.isEmpty())
            return "Authorization failed";

        wins = myData.returnWins(username);
        losses = myData.returnLosses(username);

        if (losses <= 0)
            return "Play more games to see Win/Loss Ratio";
        else
            return "Your Win/Loss ratio: " + wins / losses;
    }

    public String returnEloScore(String token) {
        int eloScore;
        String username = myData.returnUsernameFromToken(token);

        if (username == null || username.isEmpty())
            return "Authorization failed";

        eloScore = myData.returnElo(username);

        return "Your Elo: " + Math.max(eloScore, 0);
    }

    public Object returnGlobalScoreboard(String token) {
        List<String> scoreboard;

        String username = myData.returnUsernameFromToken(token);

        if (username == null || username.isEmpty())
            return "Authorization failed";

        scoreboard = myData.returnScoreboard();

        return scoreboard;
    }

    public Object returnDeck(String token) {
        List<String> deck;

        int userID = myData.returnUserIDFromToken(token);


        deck = myData.returnPlayerCards(userID);

        return deck;
    }

    public Object returnAllUserCards(String token) {
        List<String> deck;

        int userID = myData.returnUserIDFromToken(token);


        deck = myData.returnAllPlayerCards(userID);

        if (deck.isEmpty())
            return "You have no Cards yet";

        return deck;
    }

    public String updateUserProfile(String token, String newUsername, String newBio, String newImage, String httpMethodWithPath) {
        int userID = myData.returnUserIDFromToken(token);
        String username = myData.returnUsernameFromToken(token);

        if (!httpMethodWithPath.contains(username))
            return "You are not allowed to change data from this user";
        if (userID == 0)
            return "Log in first";

        myData.updateUserData(userID, newUsername, newBio, newImage);

        return "Your profile has been updated";

    }

    public String addToDeck(String token, String ID) {
        int userID = myData.returnUserIDFromToken(token);
        if (userID == 0)
            return "Log in first";

        myData.configureDeck(userID, ID);

        return "Card added to deck";

    }

    public static List<Card> generateUserDeck(String username) {
        DataBase myData = new DataBase();

        List<String> playerCards;
        playerCards = myData.returnPlayerCards(myData.returnUserID(username));
        List<Card> playerCard = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int damage;
            Element cardElement;
            CardName cardName;
            SpellCard newSpellCard;
            MonsterCard newMonsterCard;

            String[] splitCards = playerCards.get(i).split(";", 3);
            damage = Integer.parseInt(splitCards[0]);
            cardName = CardName.valueOf(splitCards[1]);
            cardElement = Element.valueOf(splitCards[2]);

            if (cardName.equals(CardName.SPELL)) {
                newSpellCard = new SpellCard(cardName, damage, cardElement);
                playerCard.add(newSpellCard);
            } else {
                newMonsterCard = new MonsterCard(cardName, damage, cardElement);
                playerCard.add(newMonsterCard);
            }

        }
        System.out.println(playerCard.size());

        return playerCard;
    }
}
