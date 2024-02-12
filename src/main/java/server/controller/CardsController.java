package server.controller;

import database.DataBase;
import mtcg.Card;
import server.response.ResponseModel;

import java.util.List;

public class CardsController {
    DataBase myData = new DataBase();
    public ResponseModel generateNewCard(String cardID, String cardName, int cardDamage, String cardElement, int packageID, String authorizationToken) {
        if(!authorizationToken.endsWith("-mtcgToken"))
            return new ResponseModel("Access token is missing or invalid", 401);

        boolean validation = myData.validateToken("admin", authorizationToken);
        if (validation) {
            myData.generateCards(cardID, cardName, cardDamage, cardElement, packageID);
            return new ResponseModel("Package and cards successfully created", 201);
        } else {
            return new ResponseModel("Provided user is not admin", 403);
        }
    }

    public ResponseModel acquirePackage(String token) {
        String username = myData.returnUsernameFromToken(token);
        int coins = myData.returnCoins(username);

        if(coins < 5) {
            return new ResponseModel("Not enough money for buying a card package", 403);
        }

        int packageID = myData.getNextAvailablePackageID();
        if(packageID < 0) {
            return new ResponseModel("No card package available for buying", 404);
        }

        List<Card> cards = myData.givePackageToUser(token, packageID);
        if(cards == null || cards.isEmpty()) {
            System.out.println("The card list is null or empty after assigning to user."); // Debugging line
        }
        return new ResponseModel("A package has been successfully bought", 200, cards);
    }
}
