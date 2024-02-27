package repository.repositoryImpl;

import database.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserRepositoryImplTests {

    @Mock
    private DatabaseUtil databaseUtil;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() throws Exception {
        when(databaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testCheckIfUserExists() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        assertTrue(userRepository.checkIfUserExists("username"));

        when(resultSet.getInt(1)).thenReturn(0);
        assertFalse(userRepository.checkIfUserExists("username"));
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Simulate successful user creation
        when(preparedStatement.executeUpdate()).thenReturn(0);

        userRepository.registerUser("newUser", "password123");

        // Verify that executeUpdate was called, indicating the user was created
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testCreateUser_DuplicateUsername() throws Exception {
        // Simulate SQLException for duplicate username
        doThrow(new SQLException()).when(preparedStatement).executeUpdate();

        assertThrows(RuntimeException.class, () -> userRepository.registerUser("duplicateUser", "password123"));

        // Verify that executeUpdate was called, indicating an attempt to create the user
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testReturnUserIDFromToken() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("UserID")).thenReturn(1);

        int userId = userRepository.returnUserIDFromToken("token123");

        assertEquals(1, userId);

        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testValidateTokenFromUsername_ValidToken() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("Token")).thenReturn("Bearer token123");

        boolean isValid = userRepository.validateTokenFromUsername("user", "Bearer token123");

        assertTrue(isValid);

        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testValidateTokenFromUsername_InvalidToken() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("Token")).thenReturn("Bearer token123");

        boolean isValid = userRepository.validateTokenFromUsername("user", "Bearer wrongToken");

        assertFalse(isValid);

        verify(preparedStatement, times(1)).executeQuery();
    }

}
