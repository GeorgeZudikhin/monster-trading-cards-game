package repository;

import businessLogic.Card;

import java.util.List;

public interface CardRepository {
    List<Card> getUserDeckFromUserId(int userID);
    List<Card> getAllUserCardsByUserID(int userID);
    void generateCard(String cardID, String cardName, int cardDamage, String cardElement, int packageID);
    int getNextAvailablePackageID();
    List<Card> assignPackageToUser(String authToken, int packageID);
    List<Card> getCardsByPackageID(int packageID);
    int getCurrentDeckSize(int userID);
    void setCardInDeck(int userID, String cardID);
}
