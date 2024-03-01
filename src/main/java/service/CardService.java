package service;

import gameElements.card.Card;
import repository.CardRepository;
import repository.UserRepository;
import http.response.ResponseModel;

import java.util.List;
import java.util.Random;

public class CardService {

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
        if (validation) {
            cardRepository.generateCard(cardID, cardName, cardDamage, cardElement, packageID);
            return new ResponseModel("Package and cards successfully created", 201);
        } else {
            return new ResponseModel("Provided user is not admin", 403);
        }
    }

    public ResponseModel acquirePackage(String authToken) {
        String username = userRepository.returnUsernameFromToken(authToken);
        int coins = userRepository.returnUserCoins(username);

        if(coins < 5) {
            return new ResponseModel("Not enough money for buying a card package", 403);
        }

        int packageID = cardRepository.getNextAvailablePackageID();
        if(packageID < 0) {
            return new ResponseModel("No card package available for buying", 404);
        }

        List<Card> cards = cardRepository.assignPackageToUser(authToken, packageID);
        return new ResponseModel("A package has been successfully bought", 200, cards);
    }

    public ResponseModel gambleCard(String authToken) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        List<Card> deck = cardRepository.getUserDeckByUserID(userID);
        if (deck.isEmpty())
            return new ResponseModel("The request was fine, but the user's deck is empty", 204);

        Random random = new Random();
        Card selectedCard = deck.get(random.nextInt(deck.size()));
        boolean isGamblingSuccessful;

        // 50/50 chance to double or halve the card's damage
        if (random.nextBoolean()) {
            selectedCard.setDamage(selectedCard.getDamage() * 2); // Double the damage
            isGamblingSuccessful = true;
        } else {
            selectedCard.setDamage(selectedCard.getDamage() / 2); // Halve the damage
            isGamblingSuccessful = false;
        }

        // Update the card in the database
        boolean updateSuccess = cardRepository.updateCardDamage(selectedCard.getId(), selectedCard.getDamage());
        if (!updateSuccess) {
            return new ResponseModel("Failed to update the card's damage", 500);
        }

        if(isGamblingSuccessful)
            return new ResponseModel("Gambling was successful! Your card's damage was doubled!", 200, selectedCard);

        return new ResponseModel("Oops, gambling was not successful! Your card's damage was halved!", 200, selectedCard);
    }
}
