package repository;

import models.UserModel;

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
    boolean validateTokenFromUsername(String username, String token);
    UserModel returnUserDataByUsername(String requestedUsername);
    void updateUserData(int userID, String newUsername, String newBio, String newImage);
    int returnUserCoins(String username);
    void updateUserCoins(int userID);
}
