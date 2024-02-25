package repository.repositoryImpl;

import java.sql.*;

import database.DatabaseUtil;
import gameElements.*;
import repository.CardRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class CardRepositoryImpl implements CardRepository {
    private static CardRepositoryImpl cardRepository;
    private final UserRepository userRepository;

    private CardRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static synchronized CardRepositoryImpl getInstance(UserRepository userRepository) {
        if (cardRepository == null) {
            cardRepository = new CardRepositoryImpl(userRepository);
        }
        return cardRepository;
    }

    @Override
    public List<Card> getUserDeckByUserID(int userID) {
        List<Card> userDeck = new ArrayList<>();
        final String query = "SELECT * FROM \"Card\" WHERE \"UserID\" = ? AND \"InDeck\" = 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CardName cardName = CardName.valueOf(resultSet.getString("Name"));
                    int damage = resultSet.getInt("Damage");
                    ElementType elementType = ElementType.valueOf(resultSet.getString("ElementType"));

                    Card card;
                    if ("Spell".equals(cardName.toString())) {
                        card = new SpellCard(cardName, damage, elementType);
                    } else {
                        card = new MonsterCard(cardName, damage, elementType);
                    }
                    userDeck.add(card);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user deck for userID: " + userID, e);
        }
        return userDeck;
    }

    @Override
    public void generateCard(String cardID, String cardName, int cardDamage, String cardElement, int packageID) {
        final String query = "INSERT INTO \"Card\" (\"CardID\", \"Name\", \"Damage\", \"ElementType\", \"PackageID\") VALUES (?, ?, ?, ?, ?)";
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

    @Override
    public int getNextAvailablePackageID() {
        final String query = "SELECT \"PackageID\" FROM \"Card\" WHERE \"UserID\" IS NULL GROUP BY \"PackageID\" ORDER BY \"PackageID\" LIMIT 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("PackageID");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving next available package ID", e);
        }
        return -1;
    }

    @Override
    public List<Card> assignPackageToUser(String token, int packageID) {
        int userID = userRepository.returnUserIDFromToken(token);
        final String query = "UPDATE \"Card\" SET \"UserID\" = ? WHERE \"PackageID\" = ? AND \"UserID\" IS NULL";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.setInt(2, packageID);
            statement.executeUpdate();

            userRepository.updateUserCoins(userID);
            return getCardsByPackageID(packageID);
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning package to user", e);
        }
    }

    @Override
    public List<Card> getCardsByPackageID(int packageID) {
        List<Card> cards = new ArrayList<>();
        final String query = "SELECT \"CardID\", \"Name\", \"Damage\", \"ElementType\" FROM \"Card\" WHERE \"PackageID\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, packageID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CardName name = CardName.valueOf(resultSet.getString("Name"));
                    int damage = resultSet.getInt("Damage");
                    ElementType elementType = ElementType.valueOf(resultSet.getString("ElementType"));

                    Card card;
                    if ("Spell".equals(name.toString())) {
                        card = new SpellCard(name, damage, elementType);
                    } else {
                        card = new MonsterCard(name, damage, elementType);
                    }
                    cards.add(card);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cards by package ID", e);
        }
        return cards;
    }

    @Override
    public List<Card> getAllUserCardsByUserID(int userID) {
        List<Card> cards = new ArrayList<>();
        final String query = "SELECT \"CardID\", \"Name\", \"Damage\", \"ElementType\" FROM \"Card\" WHERE \"UserID\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CardName name = CardName.valueOf(resultSet.getString("Name"));
                    int damage = resultSet.getInt("Damage");
                    ElementType elementType = ElementType.valueOf(resultSet.getString("ElementType"));

                    Card card;
                    if ("Spell".equals(name.toString())) {
                        card = new SpellCard(name, damage, elementType);
                    } else {
                        card = new MonsterCard(name, damage, elementType);
                    }
                    cards.add(card);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cards for user ID: " + userID, e);
        }
        return cards;
    }

    @Override
    public int getCurrentDeckSize(int userID) {
        int count = 0;
        final String query = "SELECT COUNT(*) AS deckSize FROM \"Card\" WHERE \"UserID\" = ? AND \"InDeck\" = 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("deckSize");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving deck size for userID: " + userID, e);
        }
        return count;
    }

    @Override
    public void setCardInDeck(int userID, String cardID) {
        final String query = "UPDATE \"Card\" SET \"InDeck\" = 1 WHERE \"UserID\" = ? AND \"CardID\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.setString(2, cardID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating card in deck for userID: " + userID + " and cardID: " + cardID, e);
        }
    }


}
