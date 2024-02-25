package repository.repositoryImpl;

import java.sql.*;

import database.DatabaseUtil;
import model.TradingDealModel;
import repository.TradingRepository;

import java.util.ArrayList;
import java.util.List;

public class TradingRepositoryImpl implements TradingRepository {
    private static TradingRepositoryImpl tradingRepository;

    private TradingRepositoryImpl() {}

    public static synchronized TradingRepositoryImpl getInstance() {
        if (tradingRepository == null) {
            tradingRepository = new TradingRepositoryImpl();
        }
        return tradingRepository;
    }

    @Override
    public void createTradingDeal(int userID, TradingDealModel tradingDeal) {
        final String query = "INSERT INTO \"TradingDeal\" (\"Id\", \"CardToTrade\", \"Type\", \"MinimumDamage\", \"UserID\") VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tradingDeal.getId());
            statement.setString(2, tradingDeal.getCardToTrade());
            statement.setString(3, tradingDeal.getType());
            statement.setInt(4, tradingDeal.getMinimumDamage());
            statement.setInt(5, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating trading deal", e);
        }
    }

    public boolean checkIfTradingDealExists(String tradingDealId) {
        final String query = "SELECT COUNT(*) FROM \"TradingDeal\" WHERE \"Id\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tradingDealId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if trading deal exists", e);
        }
        return false;
    }

    @Override
    public List<TradingDealModel> getAllTradingDeals() {
        List<TradingDealModel> deals = new ArrayList<>();
        final String query = "SELECT \"Id\", \"CardToTrade\", \"Type\", \"MinimumDamage\" FROM \"TradingDeal\"";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                TradingDealModel tradingDeal = TradingDealModel.builder()
                        .Id(resultSet.getString("Id"))
                        .cardToTrade(resultSet.getString("CardToTrade"))
                        .type(resultSet.getString("Type"))
                        .minimumDamage(resultSet.getInt("MinimumDamage"))
                        .build();
                deals.add(tradingDeal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching trading deals", e);
        }
        return deals;
    }

    @Override
    public void deleteTradingDeal(String dealID) {
        final String query = "DELETE FROM \"TradingDeal\" WHERE \"Id\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, dealID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting trading deal", e);
        }
    }
}
