package repository;

import model.UserModel;

public interface UserRepository {
    boolean checkIfUserExists(String username);
    void registerUser(String username, String password);
    boolean loginUser(String username, String requestPassword);
    int returnUserIDFromToken(String token);
    String returnUsernameFromToken(String authorizationToken);
    String returnUsernameFromID(int userID);
    int returnUserIDFromUsername(String username);
    void setUserToken(String username);
    String returnTokenFromUsername(String username);
    boolean validateTokenFromUsername(String username, String authToken);
    UserModel returnUserDataByUsername(String requestedUsername);
    void updateUserData(int userID, String newUsername, String newBio, String newImage);
    int returnUserCoins(String username);
    void updateUserCoins(int userID);
    int getUserEloByUsername(String username);
    int getUserWinsByUsername(String username);
    int getUserLossesByUsername(String username);
    void updateUserEloByUsername(String username, int elo);
    void setUserWinsByUsername(String username, int wins);
    void setUserLossesByUsername(String username, int losses);
}
