package repository;

import gameElements.Card;

import java.util.List;

public interface CardRepository {
    List<Card> getUserDeckByUserID(int userID);
    List<Card> getAllUserCardsByUserID(int userID);
    void generateCard(String cardID, String cardName, int cardDamage, String cardElement, int packageID);
    int getNextAvailablePackageID();
    List<Card> assignPackageToUser(String authToken, int packageID);
    List<Card> getCardsByPackageID(int packageID);
    int getCurrentDeckSize(int userID);
    void setCardInDeck(int userID, String cardID);
    boolean updateCardOwnership(String cardId, int newOwnerId);
    boolean isCardEligibleForTrading(String cardId, String requiredType, int minDamage);

    boolean updateCardDamage(String cardID, double damage);
}
