package service;

import businessLogic.*;
import database.DataBase;
import repositoryInterface.UserRepository;
import models.UserModel;
import http.ResponseModel;

import java.util.List;

public class UserService {
    DataBase myData = new DataBase();

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseModel regUser(String username, String password) {
        if(userRepository.checkIfUserExists(username))
            return new ResponseModel("User already exists", 409);

        userRepository.registerUser(username, password);
        System.out.println("I have just registered a user using a userRepository!");
        return new ResponseModel("User successfully registered", 201);
    }

    public ResponseModel logUser(String username, String requestPassword) {
        boolean isUserAuthorized = userRepository.loginUser(username, requestPassword);
        if (!isUserAuthorized)
            return new ResponseModel("Invalid username or password", 401);

        String token = userRepository.returnTokenFromUsername(username);
        if (token == null)
            return new ResponseModel("Error retrieving token", 500);


        System.out.println("I have just logged in a user using a userRepository!");

        return new ResponseModel(token, 200);
    }

    public String returnWinLossRatio(String token) {
        float wins;
        float losses;
        String username = myData.returnUsernameFromToken(token);

        if (username == null || username.isEmpty())
            return "Authorization failed";

        wins = myData.returnUserWins(username);
        losses = myData.returnUserLosses(username);

        if (losses <= 0)
            return "Play more games to see Win/Loss Ratio";
        else
            return "Your Win/Loss ratio: " + wins / losses;
    }

    public String returnEloScore(String token) {
        int eloScore;
        String username = userRepository.returnUsernameFromToken(token);

        if (username == null || username.isEmpty())
            return "Authorization failed";

        eloScore = myData.returnElo(username);

        return "Your Elo: " + Math.max(eloScore, 0);
    }

    public Object returnGlobalScoreboard(String token) {
        List<String> scoreboard;

        String username = userRepository.returnUsernameFromToken(token);

        if (username == null || username.isEmpty())
            return "Authorization failed";

        scoreboard = myData.returnScoreboard();

        return scoreboard;
    }

    public ResponseModel returnUserDeck(String token) {
        List<Card> userDeck;

        int userID = userRepository.returnUserIDFromToken(token);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        userDeck = myData.getUserDeckFromUserId(userID);
        if (userDeck.isEmpty())
            return new ResponseModel("The request was fine, but the user's deck doesn't have any cards", 404);

        return new ResponseModel("The deck has cards, the response contains these", 200, userDeck);
    }

    public ResponseModel returnAllUserCards(String token) {
        List<Card> cards;

        int userID = userRepository.returnUserIDFromToken(token);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        cards = myData.getCardsByUserID(userID);
        if (cards.isEmpty())
            return new ResponseModel("The request was fine, but the user doesn't have any cards", 204);

        return new ResponseModel("The user has cards, the response contains these", 200, cards);
    }

    public ResponseModel getUserProfileByUsername(String token, String requestedUsername) {
        int userID = userRepository.returnUserIDFromToken(token);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        String tokenUsername = userRepository.returnUsernameFromToken(token);
        System.out.println(tokenUsername);
        if (!tokenUsername.equalsIgnoreCase(requestedUsername)) {
            return new ResponseModel("You are not authorized to view this user's profile", 403);
        }

        UserModel userData = userRepository.returnUserDataByUsername(requestedUsername);
        if (userData == null) {
            return new ResponseModel("User not found", 404);
        }

        return new ResponseModel("User profile retrieved successfully", 200, userData);
    }

    public ResponseModel updateUserProfile(String token, String requestedUsername, String newUsername, String newBio, String newImage) {
        int userID = myData.returnUserIDFromToken(token);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        String tokenUsername = userRepository.returnUsernameFromToken(token);
        System.out.println(tokenUsername);
        if (!tokenUsername.equalsIgnoreCase(requestedUsername)) {
            return new ResponseModel("You are not authorized to view this user's profile", 403);
        }

        userRepository.updateUserData(userID, newUsername, newBio, newImage);

        return new ResponseModel("Your profile has been updated", 200);
    }

    public ResponseModel addToDeck(String token, String cardID) {
        int userID = userRepository.returnUserIDFromToken(token);
        if (userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        int currentDeckSize = myData.getDeckSize(userID);
        System.out.println("Current deck size: " + currentDeckSize);
        if (currentDeckSize >= 4) {
            return new ResponseModel("At least one of the provided cards does not belong to the user or is not available", 403);
        }

        myData.configureDeck(userID, cardID);

        return new ResponseModel("The deck has been successfully configured", 200);
    }

    public static List<Card> generateUserDeck(String username) {
        DataBase myData = new DataBase();

        List<Card> playerCards;
        playerCards = myData.getUserDeckFromUserId(myData.returnUserIDFromUsername(username));
//        List<Card> playerCard = new ArrayList<>();
//
//        for (int i = 0; i < 4; i++) {
//            int damage;
//            Element cardElement;
//            CardName cardName;
//            SpellCard newSpellCard;
//            MonsterCard newMonsterCard;
//
//            String[] splitCards = playerCards.get(i).split(";", 3);
//            damage = Integer.parseInt(splitCards[0]);
//            cardName = CardName.valueOf(splitCards[1]);
//            cardElement = Element.valueOf(splitCards[2]);
//
//            if (cardName.equals(CardName.SPELL)) {
//                newSpellCard = new SpellCard(cardName, damage, cardElement);
//                playerCard.add(newSpellCard);
//            } else {
//                newMonsterCard = new MonsterCard(cardName, damage, cardElement);
//                playerCard.add(newMonsterCard);
//            }
//
//        }
        System.out.println(playerCards.size());

        return playerCards;
    }

}
