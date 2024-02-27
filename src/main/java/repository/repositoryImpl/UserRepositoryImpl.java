package repository.repositoryImpl;

import java.sql.*;

import database.DatabaseUtil;
import repository.UserRepository;
import model.UserModel;

public class UserRepositoryImpl implements UserRepository {
    private static UserRepositoryImpl userRepository;
    private final DatabaseUtil databaseUtil;

    private UserRepositoryImpl(DatabaseUtil databaseUtil) {
        this.databaseUtil = databaseUtil;
    }

    public static synchronized UserRepositoryImpl getInstance(DatabaseUtil databaseUtil) {
        if (userRepository == null) {
            userRepository = new UserRepositoryImpl(databaseUtil);
        }
        return userRepository;
    }

    @Override
    public boolean checkIfUserExists(String username) {
        final String query = "SELECT COUNT(*) FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user exists", e);
        }
        return false;
    }

    @Override
    public void registerUser(String username, String password) {
        final String query = "INSERT INTO \"User\" (\"Username\", \"Password\") VALUES (?, ?)";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public boolean loginUser(String username, String requestPassword) {
        final String query = "SELECT \"Password\" FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String passwordToCheck = resultSet.getString("Password");
                if (passwordToCheck != null && passwordToCheck.equals(requestPassword)) {
                    setUserToken(username);
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error logging in user", e);
        }
        return false;
    }

    @Override
    public int returnUserIDFromToken(String token) {
        final String query = "SELECT \"UserID\" FROM \"User\" WHERE \"Token\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("UserID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user ID from token", e);
        }
        return 0;
    }

    @Override
    public String returnUsernameFromID(int userID) {
        final String query = "SELECT \"Username\" FROM \"User\" WHERE \"UserID\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Username");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving username from ID", e);
        }
        return null;
    }

    @Override
    public int returnUserIDFromUsername(String username) {
        final String query = "SELECT \"UserID\" FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("UserID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user ID from username", e);
        }
        return 0;
    }


    @Override
    public String returnUsernameFromToken(String token) {
        final String query = "SELECT \"Username\" FROM \"User\" WHERE \"Token\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Username");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving username from token", e);
        }
        return "";
    }

    @Override
    public String returnTokenFromUsername(String username) {
        final String query = "SELECT \"Token\" FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Token");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving token for username", e);
        }
        return null;
    }

    @Override
    public void setUserToken(String username) {
        final String query = "UPDATE \"User\" SET \"Token\" = ? WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "Bearer " + username + "-mtcgToken");
            statement.setString(2, username);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting user token", e);
        }
    }

    @Override
    public boolean validateTokenFromUsername(String username, String token) {
        final String query = "SELECT \"Token\" FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String savedToken = resultSet.getString("Token");
                    return savedToken.equals(token);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating token for username", e);
        }
        return false;
    }

    @Override
    public UserModel returnUserDataByUsername(String requestedUsername) {
        final String query = "SELECT * FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, requestedUsername);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    UserModel user = new UserModel();
                    user.setUsername(resultSet.getString("Username"));
                    user.setPassword(resultSet.getString("Password"));
                    user.setNewBio(resultSet.getString("Bio"));
                    user.setNewImage(resultSet.getString("Image"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user data by username", e);
        }
        return null;
    }

    @Override
    public void updateUserData(int userID, String newUsername, String newBio, String newImage) {
        final String query = "UPDATE \"User\" SET \"Username\" = ?, \"Bio\" = ?, \"Image\" = ? WHERE \"UserID\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newUsername);
            statement.setString(2, newBio);
            statement.setString(3, newImage);
            statement.setInt(4, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user data", e);
        }
    }

    @Override
    public int returnUserCoins(String username) {
        final String query = "SELECT \"Coins\" FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("Coins");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user coins", e);
        }
        return 0;
    }

    @Override
    public void updateUserCoins(int userID) {
        final String query = "UPDATE \"User\" SET \"Coins\" = \"Coins\" - 5 WHERE \"UserID\" = ?";
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user coins", e);
        }
    }

    @Override
    public int getUserEloByUsername(String username) {
        final String query = "SELECT \"Elo\" FROM \"User\" WHERE \"Username\" = ?";
        return executeQueryForSingleIntResult(query, username);
    }

    @Override
    public int getUserWinsByUsername(String username) {
        final String query = "SELECT \"Wins\" FROM \"User\" WHERE \"Username\" = ?";
        return executeQueryForSingleIntResult(query, username);
    }

    @Override
    public int getUserLossesByUsername(String username) {
        final String query = "SELECT \"Losses\" FROM \"User\" WHERE \"Username\" = ?";
        return executeQueryForSingleIntResult(query, username);
    }

    private int executeQueryForSingleIntResult(String query, String parameter) {
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameter);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }
        return 0;
    }

    @Override
    public void updateUserEloByUsername(String username, int elo) {
        final String query = "UPDATE \"User\" SET \"Elo\" = ? WHERE \"Username\" = ?";
        executeUpdate(query, elo, username);
    }

    @Override
    public void setUserWinsByUsername(String username, int wins) {
        final String query = "UPDATE \"User\" SET \"Wins\" = ? WHERE \"Username\" = ?";
        executeUpdate(query, wins, username);
    }

    @Override
    public void setUserLossesByUsername(String username, int losses) {
        final String query = "UPDATE \"User\" SET \"Losses\" = ? WHERE \"Username\" = ?";
        executeUpdate(query, losses, username);
    }

    private void executeUpdate(String query, int value, String username) {
        try (Connection connection = databaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, value);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user stats for username: " + username, e);
        }
    }

}
