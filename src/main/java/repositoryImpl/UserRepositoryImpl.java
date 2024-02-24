package repositoryImpl;

import java.sql.*;

import database.DatabaseUtil;
import repository.UserRepository;
import models.UserModel;

public class UserRepositoryImpl implements UserRepository {
    private static UserRepositoryImpl userRepository;

    private UserRepositoryImpl() {}

    public static synchronized UserRepositoryImpl getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepositoryImpl();
        }
        return userRepository;
    }

    @Override
    public boolean checkIfUserExists(String username) {
        final String query = "SELECT COUNT(*) FROM \"User\" WHERE \"Username\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
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
        try (Connection connection = DatabaseUtil.getConnection();
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String passwordToCheck = rs.getString("Password");
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Username");
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Username");
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Token");
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
        try (Connection connection = DatabaseUtil.getConnection();
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String savedToken = rs.getString("Token");
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, requestedUsername);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    UserModel user = new UserModel();
                    user.setUsername(rs.getString("Username"));
                    user.setNewBio(rs.getString("Bio"));
                    user.setNewImage(rs.getString("Image"));
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
        try (Connection connection = DatabaseUtil.getConnection();
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("Coins");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user coins", e);
        }
        return 0;
    }

    @Override
    public void updateUserCoins(int userID) {
        final String query = "UPDATE \"User\" SET \"Coins\" = \"Coins\" - 5 WHERE \"UserID\" = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user coins", e);
        }
    }

}
