package repositoryInterface;

import businessLogic.Card;

import java.util.List;

public interface CardRepository {
    List<Card> getUserDeckFromUserId(int userID);
    void generateCard(String cardID, String cardName, int cardDamage, String cardElement, int packageID);
}
