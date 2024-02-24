package database;

import businessLogic.*;
import model.UserModel;

import java.sql.*;
import java.util.ArrayList;

import java.util.List;

public class DataBase {

    public int returnUserIDFromToken(String authorizationToken) {
        int UserID = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "UserID" From "User"
                WHERE "Token" = ?;
                """)
        ) {
            statement.setString(1, authorizationToken);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                UserID = rs.getInt("UserID");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return UserID;
    }

    public List<Card> getUserDeckFromUserID(int userID) {

        List<Card> userDeck = new ArrayList<>();
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT * From "Cards"
                WHERE "UserID" = ?
                AND "InDeck" = 1;
                """)
        ) {
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                CardName name = CardName.valueOf(rs.getString("Name"));
                int damage = rs.getInt("Damage");
                Element elementType = Element.valueOf(rs.getString("ElementType"));

                Card card;
                if ("Spell".equals(name)) {
                    card = new SpellCard(name, damage, elementType);
                } else {
                    card = new MonsterCard(name, damage, elementType);
                }
                userDeck.add(card);
            }

            if(userDeck.isEmpty()) {
                System.out.println("No cards found for userID: " + userID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userDeck;
    }


    public boolean isCardInDeck(int userID, String cardID) {
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
            SELECT COUNT(*) FROM "Cards"
            WHERE "UserID" = ? AND "CardID" = ? AND "InDeck" = 1;
            """)
        ) {
            statement.setInt(1, userID);
            statement.setString(2, cardID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Check if count is greater than 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getDeckSize(int userID) {
        int count = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
            SELECT COUNT(*) AS deckSize FROM "Cards"
            WHERE "UserID" = ? AND "InDeck" = 1;
            """)
        ) {
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                count = rs.getInt("deckSize");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }



    public void setCardInDeck(int userID, String cardID) {

        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "Cards"
                SET "InDeck" = 1
                WHERE "UserID" = ? AND "CardID" = ?;
                """)
        ) {
            statement.setInt(1, userID);
            statement.setString(2, cardID);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int returnUserIDFromUsername(String username) {
        int UserID = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "UserID" From "User"
                WHERE "Username" = ?;
                """)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                UserID = rs.getInt("UserID");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return UserID;
    }

    public int returnElo(String username) {
        int currentElo = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Elo" From "User"
                WHERE "Username" = ?;
                """)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                currentElo = rs.getInt("Elo");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentElo;
    }

    public void updateElo(String username, int elo) {
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "User"
                SET "Elo" = ?
                WHERE "Username" = ?;
                """)
        ) {
            statement.setInt(1, elo);
            statement.setString(2, username);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int returnUserWins(String username) {
        int wins = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Wins" From "User"
                WHERE "Username" = ?;
                """))
        {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                wins = rs.getInt("Wins");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wins;
    }

    public int returnUserLosses(String username) {
        int losses = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Losses" From "User"
                WHERE "Username" = ?;
                """)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                losses = rs.getInt("Losses");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return losses;
    }

    public int returnUserCoins(String username) {
        int coins = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Coins" From "User"
                WHERE "Username" = ?;
                """)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                coins = rs.getInt("Coins");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coins;
    }

    public void countWins(String username, float newWinScore) {
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "User"
                SET "Wins" = ?
                WHERE "Username" = ?;
                """)
        ) {
            statement.setFloat(1, newWinScore);
            statement.setString(2, username);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void countLosses(String username, float newLosScore) {
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "User"
                SET "Losses" = ?
                WHERE "Username" = ?;
                """)
        ) {
            statement.setFloat(1, newLosScore);
            statement.setString(2, username);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readyToPlay(String token, int ready) {
        int userID = returnUserIDFromToken(token);

        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "User"
                SET "Ready" = ?
                WHERE "UserID" = ?;
                """)
        ) {
            statement.setInt(1, ready);
            statement.setInt(2, userID);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int returnPlayerReady(String username) {
        int readyPlayer = 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                SELECT SUM("Ready") AS "Ready_player"
                FROM "User"
                WHERE "Ready" = ?;               
                """)
        ) {
            statement.setInt(1, 1);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                readyPlayer = rs.getInt("Ready_player");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readyPlayer;
    }

    public List<String> returnUsernamePlayerReady() {
        String username = "";
        String token = "";
        List<String> readyPlayers = new ArrayList<>();
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Username", "Token" From "User"
                WHERE "Ready" = ?;             
                """)
        ) {
            statement.setInt(1, 1);
            ResultSet rs = statement.executeQuery();


            while (rs.next()) {
                username = rs.getString("Username");
                token = rs.getString("Token");

                readyPlayers.add(username);
                readyToPlay(token, 0);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readyPlayers;
    }

}
