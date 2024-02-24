package repositoryImplementation;

import java.sql.*;

import database.DatabaseUtil;
import businessLogic.*;
import repositoryInterface.CardRepository;

import java.util.ArrayList;
import java.util.List;

public class CardRepositoryImpl implements CardRepository {
    private static CardRepositoryImpl cardRepository;

    private CardRepositoryImpl() {}

    public static synchronized CardRepositoryImpl getInstance() {
        if (cardRepository == null) {
            cardRepository = new CardRepositoryImpl();
        }
        return cardRepository;
    }

    @Override
    public List<Card> getUserDeckFromUserId(int userID) {
        List<Card> userDeck = new ArrayList<>();
        final String query = "SELECT * FROM \"Cards\" WHERE \"UserID\" = ? AND \"InDeck\" = 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String cardName = rs.getString("Name");
                    CardName name = CardName.valueOf(cardName);
                    int damage = rs.getInt("Damage");
                    Element elementType = Element.valueOf(rs.getString("ElementType"));

                    Card card;
                    if ("Spell".equals(cardName)) {
                        card = new SpellCard(name, damage, elementType);
                    } else {
                        card = new MonsterCard(name, damage, elementType);
                    }
                    userDeck.add(card);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user deck", e);
        }
        return userDeck;
    }

    @Override
    public void generateCard(String cardID, String cardName, int cardDamage, String cardElement, int packageID) {
        final String query = "INSERT INTO \"Cards\" (\"CardID\", \"Name\", \"Damage\", \"ElementType\", \"PackageID\") VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cardID);
            statement.setString(2, cardName);
            statement.setInt(3, cardDamage);
            statement.setString(4, cardElement);
            statement.setInt(5, packageID);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error generating cards", e);
        }
    }
}
