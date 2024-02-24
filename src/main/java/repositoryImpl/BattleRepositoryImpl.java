package repositoryImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import businessLogic.*;
import database.DatabaseUtil;
import model.StatsModel;
import repository.BattleRepository;

public class BattleRepositoryImpl implements BattleRepository {
    private static BattleRepositoryImpl battleRepository;

    //private final UserRepository userRepository;

//    private BattleRepositoryImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
    private BattleRepositoryImpl() {}

    public static synchronized BattleRepositoryImpl getInstance() {
        if (battleRepository == null) {
            battleRepository = new BattleRepositoryImpl();
        }
        return battleRepository;
    }

    @Override
    public StatsModel returnUserStats(String username) {
        final String query = "SELECT * FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Integer elo = resultSet.getObject("Elo", Integer.class);
                    Integer wins = resultSet.getObject("Wins", Integer.class);
                    Integer losses = resultSet.getObject("Losses", Integer.class);

                    return StatsModel.builder()
                            .username(username)
                            .elo(elo)
                            .wins(wins)
                            .losses(losses)
                            .build();
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving stats for username: " + username, e);
        }
    }

    @Override
    public List<StatsModel> returnScoreboard() {
        List<StatsModel> scoreboard = new ArrayList<>();
        final String query = "SELECT \"Username\", \"Elo\", \"Wins\", \"Losses\" FROM \"User\" ORDER BY \"Elo\" DESC";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            int counter = 1;
            while (resultSet.next()) {
                StatsModel stats = StatsModel.builder()
                        .position(counter++)
                        .username(resultSet.getString("Username"))
                        .elo(resultSet.getObject("Elo", Integer.class))
                        .wins(resultSet.getObject("Wins", Integer.class))
                        .losses(resultSet.getObject("Losses", Integer.class))
                        .build();
                scoreboard.add(stats);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving scoreboard", e);
        }
        return scoreboard;
    }

    @Override
    public void setPlayerToBeReadyToPlay(int userID) {
        final String query = "UPDATE \"User\" SET \"Ready\" = ? WHERE \"UserID\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, 1);
            statement.setInt(2, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting ready status for userID: " + userID, e);
        }
    }

    @Override
    public int returnHowManyPlayersAreReady() {
        final String query = "SELECT COUNT(*) AS \"ReadyPlayer\" FROM \"User\" WHERE \"Ready\" = 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("ReadyPlayer");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ready players count", e);
        }
        return 0;
    }

    @Override
    public List<String> returnUsernamesOfPlayersReady() {
        List<String> readyPlayers = new ArrayList<>();
        final String query = "SELECT \"Username\" FROM \"User\" WHERE \"Ready\" = 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString("Username");
                readyPlayers.add(username);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ready players", e);
        }
        return readyPlayers;
    }



}
