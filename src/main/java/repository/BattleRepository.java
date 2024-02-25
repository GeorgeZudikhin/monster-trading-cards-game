package repository;

import model.StatsModel;

import java.util.List;

public interface BattleRepository {
    StatsModel returnUserStats(String username);
    List<StatsModel> returnScoreboard();
    void setPlayerToBeReadyToPlay(int userID);
    int returnHowManyPlayersAreReady();
    List<String> returnUsernamesOfPlayersReady();
    void resetUserReadyStatus();
}
