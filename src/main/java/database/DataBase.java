package database;

import mtcg.*;

import java.sql.*;
import java.util.ArrayList;

import java.util.List;

public class DataBase {
    public boolean checkUserExists(String username) {
        boolean userExists = false;
        String compareUsername = "";
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Username" From "User"
                WHERE "Username" = ?;
                """)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                compareUsername = rs.getString("Username");
            if (compareUsername.equals(username)) {
                userExists = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    public int createUser(String username, String password) {
        if (checkUserExists(username))
            return 0;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                INSERT INTO "User"
                ("Username", "Password")
                VALUES(?,?);
                """)
        ) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

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

    public String returnUsernameFromToken(String authorizationToken) {
        String username = "";
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Username" From "User"
                WHERE "Token" = ?;
                """))
        {
            statement.setString(1, authorizationToken);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                username = rs.getString("Username");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    public List<String> returnPlayerCards(int UserID) {
        int damage;
        String name;
        String elementType;

        List<String> playerCards = new ArrayList<>();
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT * From "Cards"
                WHERE "UserID" = ?
                AND "InDeck" = 1;
                """)
        ) {
            statement.setInt(1, UserID);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsCounter = rsmd.getColumnCount();

            while (rs.next()) {
                damage = rs.getInt("Damage");
                name = rs.getString("Name");
                elementType = rs.getString("ElementType");

                playerCards.add(damage
                        + ";" + name +
                        ";" + elementType);
            }

            return playerCards;

            /*while (rs.next()) {
                for (int i = 1; i < columnsCounter; i++) {
                    System.out.println(rsmd.getColumnName(i) + rs.getString(i));
                }
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> returnAllPlayerCards(int UserID) {
        int damage;
        String name;
        String elementType;

        List<String> playerCards = new ArrayList<>();
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT * From "Cards"
                WHERE "UserID" = ?;
                """)
        ) {
            statement.setInt(1, UserID);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next()) {
                damage = rs.getInt("Damage");
                name = rs.getString("Name");
                elementType = rs.getString("ElementType");

                playerCards.add("Damage: " + damage
                        + ";" + "Cardname: " + name +
                        ";" + "Element: " + elementType);
            }

            return playerCards;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void generateCards(String cardID, String cardName, int cardDamage, String cardElement, int packageID) {
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                INSERT INTO "Cards"
                ("CardID", "Name", "Damage", "ElementType", "PackageID")
                VALUES(?,?,?,?,?);
                """)
        ) {
            statement.setString(1, cardID);
            statement.setString(2, cardName);
            statement.setInt(3, cardDamage);
            statement.setString(4, cardElement);
            statement.setInt(5, packageID);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> givePackageToUser(String token, int packageID) {
        int userID = returnUserIDFromToken(token);
        List<Card> cards = null;

        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "Cards"
                SET "UserID" = ?
                WHERE "PackageID" = ? AND "UserID" IS NULL;
                """)
        ) {
            statement.setInt(1, userID);
            statement.setInt(2, packageID);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("No cards were updated for the user."); // Debugging line
            }

            updateCoins(userID);
            cards = getCardsByPackageID(packageID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public List<Card> getCardsByPackageID(int packageID) {
        List<Card> cards = new ArrayList<>();
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
            SELECT "CardID", "Name", "Damage", "ElementType" FROM "Cards"
            WHERE "PackageID" = ?;
            """)) {
            statement.setInt(1, packageID);
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
                cards.add(card);
                System.out.println("Retrieved card: " + card.getName());
                if(cards.isEmpty()) {
                    System.out.println("No cards found for packageID: " + packageID); // Debugging line
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }


    public void updateCoins(int userID) {
        String username = returnUsernameFromID(userID);
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "User"
                SET "Coins" = ?
                WHERE "UserID" = ?;
                """)
        ) {
            statement.setInt(1, returnCoins(username) - 5);
            statement.setInt(2, userID);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNextAvailablePackageID() {
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
            SELECT "PackageID" FROM "Cards"
            WHERE "UserID" IS NULL
            GROUP BY "PackageID"
            ORDER BY "PackageID"
            LIMIT 1;
            """)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("PackageID");
            }
        } catch (SQLException e) {
            System.out.println("No next available packageid could be found");
            e.printStackTrace();
        }
        return -1;
    }


    public String returnUsernameFromID(int userID) {
        String username = "";
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Username" From "User"
                WHERE "UserID" = ?;
                """)
        ) {
            statement.setInt(1, userID);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                username = rs.getString("Username");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }


    public void configureDeck(int userID, String ID) {

        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "Cards"
                SET "InDeck" = 1
                WHERE "UserID" = ? AND "CardID" = ?;
                """)
        ) {
            statement.setInt(1, userID);
            statement.setString(2, ID);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int returnUserID(String username) {
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

    public List<String> returnScoreboard() {
        String name;
        int score;
        int counter = 1;

        List<String> scoreboard = new ArrayList<>();
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT * From "User"
                ORDER BY "Elo" DESC;
                """)
        ) {
            ResultSet rs = statement.executeQuery();
            scoreboard.add("Scoreboard: ");
            while (rs.next()) {
                name = rs.getString("Username");
                score = rs.getInt("Elo");

                scoreboard.add(counter + ". " + name
                        + ": " + score);
                counter++;
            }

            return scoreboard;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public boolean loginUser(String username, String password) {
        String passwordToCheck = null;
        boolean authorised = false;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Password" From "User"
                WHERE "Username" = ?;
                """)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                passwordToCheck = rs.getString("password");

            if (passwordToCheck != null && passwordToCheck.equals(password)) {
                authorised = true;
                setToken(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authorised;

    }

    public void setToken(String username) {
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "User"
                SET "Token" = ?
                WHERE "Username" = ?;
                """))
        {
            statement.setString(2, username);
            statement.setString(1, "Bearer " + username + "-mtcgToken");

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getToken(String username) {
        String token = null;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Token" FROM "User" WHERE "Username" = ?;
                """)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                token = rs.getString("Token");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }


    public boolean validateToken(String username, String token) {
        String savedToken = "";
        boolean tokenValidation = false;
        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
             PreparedStatement statement = _ctx.prepareStatement("""
                SELECT "Token" From "User"
                WHERE "Username" = ?;
                """)
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                savedToken = rs.getString("Token");

            if (savedToken.equals(token)) {
                tokenValidation = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tokenValidation;
    }


    public int returnWins(String username) {
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

    public int returnLosses(String username) {
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

    public int returnCoins(String username) {
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

    public void updateUserData(int userID, String newUsername, String newBio, String newImage) {

        try (Connection _ctx = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres"); PreparedStatement statement = _ctx.prepareStatement("""
                UPDATE "User"
                SET "Username" = ?, "Bio" = ?, "Image" = ?
                WHERE "UserID" = ?;
                """)
        ) {
            statement.setString(1, newUsername);
            statement.setString(2, newBio);
            statement.setString(3, newImage);
            statement.setInt(4, userID);
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
