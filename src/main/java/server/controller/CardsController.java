package server.controller;

import database.DataBase;

public class CardsController {
    DataBase myData = new DataBase();
    public String generatedNewCards(String cardID, String cardName, int cardDamage, String cardElement, int packageID, String authorizationToken) {
        boolean validation = myData.validateToken("admin", authorizationToken);
        if (validation) {
            myData.generateCards(cardID, cardName, cardDamage, cardElement, packageID);
            return "Package got created successfully";
        } else {
            return "You are not allowed to create Cards";
        }
    }

    public String acquirePackage(String token, int packageID) {
        String username = myData.returnUsernameFromToken(token);

        int coins = myData.returnCoins(username);

        if (coins >= 5) {
            myData.givePackageToUser(token, packageID);
            return "Success";
        } else {
            return "You do not have enough Coins";
        }
    }
}
