package repository.repositoryImpl;

import java.sql.*;

import database.DatabaseUtil;
import gameElements.card.*;
import repository.CardRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class CardRepositoryImpl implements CardRepository {
    private static CardRepositoryImpl cardRepository;
    private final UserRepository userRepository;
    private final DatabaseUtil databaseUtil;

    private CardRepositoryImpl(DatabaseUtil databaseUtil, UserRepository userRepository) {
        this.databaseUtil = databaseUtil;
        this.userRepository = userRepository;
    }

    public static synchronized CardRepositoryImpl getInstance(DatabaseUtil databaseUtil, UserRepository userRepository) {
        if (cardRepository == null) {
            cardRepository = new CardRepositoryImpl(databaseUtil, userRepository);
        }
        return cardRepository;
    }

    @Override
    public List<Card> getUserDeckByUserID(int userID) {
        List<Card> userDeck = new ArrayList<>();
        final String query = "SELECT * FROM \"Card\" WHERE \"UserID\" = ? AND \"InDeck\" = 1";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("CardID");
                    CardType cardType = CardType.valueOf(resultSet.getString("Name"));
                    int damage = resultSet.getInt("Damage");
                    CardElement cardElement = CardElement.valueOf(resultSet.getString("ElementType"));

                    Card card;
                    if ("SPELL".equals(cardType.toString())) {
                        card = new SpellCard(id, cardType, cardElement, damage);
                    } else {
                        card = new MonsterCard(id, cardType, cardElement, damage);
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
        try (Connection connection = databaseUtil.getConnection();
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
        try (Connection connection = databaseUtil.getConnection();
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
        try (Connection connection = databaseUtil.getConnection();
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
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, packageID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("CardID");
                    CardType name = CardType.valueOf(resultSet.getString("Name"));
                    int damage = resultSet.getInt("Damage");
                    CardElement cardElement = CardElement.valueOf(resultSet.getString("ElementType"));

                    Card card;
                    if ("SPELL".equals(name.toString())) {
                        card = new SpellCard(id, name, cardElement, damage);
                    } else {
                        card = new MonsterCard(id, name, cardElement, damage);
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
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("CardID");
                    CardType name = CardType.valueOf(resultSet.getString("Name"));
                    int damage = resultSet.getInt("Damage");
                    CardElement cardElement = CardElement.valueOf(resultSet.getString("ElementType"));

                    Card card;
                    if ("SPELL".equals(name.toString())) {
                        card = new SpellCard(id, name, cardElement, damage);
                    } else {
                        card = new MonsterCard(id, name, cardElement, damage);
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
        try (Connection connection = databaseUtil.getConnection();
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
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.setString(2, cardID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating card in deck for userID: " + userID + " and cardID: " + cardID, e);
        }
    }

    @Override
    public boolean updateCardOwnership(String cardId, int newOwnerId) {
        final String query = "UPDATE \"Card\" SET \"UserID\" = ? WHERE \"CardID\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newOwnerId);
            statement.setString(2, cardId);
            int updatedRows = statement.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating card ownership", e);
        }
    }

    @Override
    public boolean isCardEligibleForTrading(String cardId, String requiredCardType, int minDamage) {
        String query;
        if ("SPELL".equals(requiredCardType)) {
            query = "SELECT COUNT(*) FROM \"Card\" WHERE \"CardID\" = ? AND \"InDeck\" = 0 AND \"Damage\" >= ? AND \"Name\" = 'SPELL'";
        } else {
            query = "SELECT COUNT(*) FROM \"Card\" WHERE \"CardID\" = ? AND \"InDeck\" = 0 AND \"Damage\" >= ? AND \"Name\" != 'SPELL'";
        }
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cardId);
            statement.setInt(2, minDamage);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if card is eligible for trading", e);
        }
        return false;
    }

    public boolean updateCardDamage(String cardID, double newDamage) {
        final String query = "UPDATE \"Card\" SET \"Damage\" = ? WHERE \"CardID\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, newDamage);
            statement.setString(2, cardID);
            int updatedRows = statement.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating card's damage", e);
        }
    }
}
