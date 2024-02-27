package repository.repositoryImpl;

import database.DatabaseUtil;
import gameElements.Card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CardRepositoryImplTests {
    @Mock
    private DatabaseUtil databaseUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @InjectMocks
    private CardRepositoryImpl cardRepository;

    @BeforeEach
    void setUp() throws Exception {
        when(databaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testGetUserDeckByUserID_NonEmptyDeck() throws Exception {
        int userID = 1;
        when(resultSet.next()).thenReturn(true, true, false); // Simulate two cards in the deck, then end of results
        when(resultSet.getString("CardID")).thenReturn("card1", "card2");
        when(resultSet.getString("Name")).thenReturn("GOBLIN", "SPELL");
        when(resultSet.getInt("Damage")).thenReturn(10, 20);
        when(resultSet.getString("ElementType")).thenReturn("FIRE", "WATER");

        List<Card> deck = cardRepository.getUserDeckByUserID(userID);

        assertEquals(2, deck.size()); // Should have two cards

        verify(preparedStatement, times(1)).setInt(1, userID);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetUserDeckByUserID_EmptyDeck() throws Exception {
        int userID = 1;
        when(resultSet.next()).thenReturn(false); // Simulate no cards in the deck

        List<Card> deck = cardRepository.getUserDeckByUserID(userID);

        assertTrue(deck.isEmpty()); // Deck should be empty

        verify(preparedStatement, times(1)).setInt(1, userID);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateCardDamage_ValidCard() throws Exception {
        String cardID = "card1";
        double newDamage = 30.0;
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        boolean result = cardRepository.updateCardDamage(cardID, newDamage);

        assertTrue(result); // Update should succeed

        verify(preparedStatement, times(1)).setDouble(1, newDamage);
        verify(preparedStatement, times(1)).setString(2, cardID);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateCardDamage_InvalidCard() throws Exception {
        String cardID = "invalidCard";
        double newDamage = 30.0;
        when(preparedStatement.executeUpdate()).thenReturn(0); // Simulate failed update (no rows affected)

        boolean result = cardRepository.updateCardDamage(cardID, newDamage);

        assertFalse(result); // Update should fail

        verify(preparedStatement, times(1)).setDouble(1, newDamage);
        verify(preparedStatement, times(1)).setString(2, cardID);
        verify(preparedStatement, times(1)).executeUpdate();
    }
}
