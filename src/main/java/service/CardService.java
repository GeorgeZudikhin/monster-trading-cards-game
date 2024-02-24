package service;

import database.DataBase;
import businessLogic.Card;
import repositoryInterface.CardRepository;
import repositoryInterface.UserRepository;
import http.ResponseModel;

import java.util.List;

public class CardService {
    DataBase myData = new DataBase();

    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public CardService(UserRepository userRepository, CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public ResponseModel generateNewCard(String cardID, String cardName, int cardDamage, String cardElement, int packageID, String authorizationToken) {
        if(!authorizationToken.endsWith("-mtcgToken"))
            return new ResponseModel("Access token is missing or invalid", 401);

        boolean validation = userRepository.validateTokenFromUsername("admin", authorizationToken);
        System.out.println("Validated from userRepository!");
        if (validation) {
            cardRepository.generateCard(cardID, cardName, cardDamage, cardElement, packageID);
            System.out.println("Generated from cardRepository!");
            return new ResponseModel("Package and cards successfully created", 201);
        } else {
            return new ResponseModel("Provided user is not admin", 403);
        }
    }

    public ResponseModel acquirePackage(String token) {
        String username = myData.returnUsernameFromToken(token);
        int coins = myData.returnUserCoins(username);

        if(coins < 5) {
            return new ResponseModel("Not enough money for buying a card package", 403);
        }

        int packageID = myData.getNextAvailablePackageID();
        if(packageID < 0) {
            return new ResponseModel("No card package available for buying", 404);
        }

        List<Card> cards = myData.givePackageToUser(token, packageID);
        if(cards == null || cards.isEmpty()) {
            System.out.println("The card list is null or empty after assigning to user.");
        }
        return new ResponseModel("A package has been successfully bought", 200, cards);
    }
}
