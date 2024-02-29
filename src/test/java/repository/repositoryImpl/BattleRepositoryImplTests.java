package repository.repositoryImpl;

import database.DatabaseUtil;
import model.StatsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BattleRepositoryImplTests {

    @Mock
    private DatabaseUtil databaseUtil;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private BattleRepositoryImpl battleRepository;

    @BeforeEach
    void setUp() throws Exception {
        when(databaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testReturnUserStats_ExistingUser() throws SQLException {
        String username = "testUser";
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getObject("Elo", Integer.class)).thenReturn(1500);
        when(resultSet.getObject("Wins", Integer.class)).thenReturn(10);
        when(resultSet.getObject("Losses", Integer.class)).thenReturn(5);

        StatsModel stats = battleRepository.returnUserStats(username);

        assertNotNull(stats);
        assertEquals(1500, stats.getElo());
        assertEquals(10, stats.getWins());
        assertEquals(5, stats.getLosses());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testReturnScoreboard() throws SQLException {
        when(resultSet.next()).thenReturn(true, true, false); // Simulate two rows of data
        when(resultSet.getString("Username")).thenReturn("user1", "user2");
        when(resultSet.getObject("Elo", Integer.class)).thenReturn(1500, 1400);
        when(resultSet.getObject("Wins", Integer.class)).thenReturn(10, 9);
        when(resultSet.getObject("Losses", Integer.class)).thenReturn(5, 6);

        List<StatsModel> scoreboard = battleRepository.returnScoreboard();

        assertNotNull(scoreboard);
        assertEquals(2, scoreboard.size());
        assertEquals("user1", scoreboard.get(0).getUsername());
        assertEquals(1500, scoreboard.get(0).getElo());
        assertEquals(10, scoreboard.get(0).getWins());
        assertEquals(5, scoreboard.get(0).getLosses());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testSetPlayerToBeReadyToPlay() throws SQLException {
        int userID = 1;

        battleRepository.setPlayerToBeReadyToPlay(userID);

        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).setInt(2, userID);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testReturnHowManyPlayersAreReady() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("ReadyPlayer")).thenReturn(3);

        int readyPlayersCount = battleRepository.returnHowManyPlayersAreReady();

        assertEquals(3, readyPlayersCount);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testReturnUsernamesOfPlayersReady() throws SQLException {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("Username")).thenReturn("player1", "player2");

        List<String> readyPlayers = battleRepository.returnUsernamesOfPlayersReady();

        assertNotNull(readyPlayers);
        assertEquals(2, readyPlayers.size());
        assertTrue(readyPlayers.contains("player1"));
        assertTrue(readyPlayers.contains("player2"));
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testResetUserReadyStatus() throws SQLException {
        battleRepository.resetUserReadyStatus();

        verify(preparedStatement, times(1)).executeUpdate();
    }


}

