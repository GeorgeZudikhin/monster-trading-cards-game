package repository.repositoryImpl;

import database.DatabaseUtil;
import model.TradingDealModel;
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
class TradingRepositoryImplTests {

    @Mock
    private DatabaseUtil databaseUtil;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private TradingRepositoryImpl tradingRepository;

    @BeforeEach
    void setUp() throws Exception {
        when(databaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testCreateTradingDeal_Success() throws SQLException {
        int userID = 1;
        TradingDealModel tradingDeal = TradingDealModel.builder()
                .Id("deal1")
                .cardToTrade("card1")
                .type("type1")
                .minimumDamage(100)
                .userId(userID)
                .build();

        tradingRepository.createTradingDeal(userID, tradingDeal);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testCreateTradingDeal_DuplicateDeal() throws SQLException {
        int userID = 1;
        TradingDealModel tradingDeal = TradingDealModel.builder().Id("deal1").build();

        doThrow(new SQLException()).when(preparedStatement).executeUpdate();

        assertThrows(RuntimeException.class, () -> tradingRepository.createTradingDeal(userID, tradingDeal));
    }

    @Test
    void testGetAllTradingDeals_NonEmpty() throws SQLException {
        when(resultSet.next()).thenReturn(true, true, false);

        TradingDealModel deal1 = TradingDealModel.builder().Id("deal1").build();
        TradingDealModel deal2 = TradingDealModel.builder().Id("deal2").build();

        when(resultSet.getString("Id")).thenReturn(deal1.getId(), deal2.getId());

        var deals = tradingRepository.getAllTradingDeals();

        assertNotNull(deals);
        assertEquals(2, deals.size());
    }

    @Test
    void testGetAllTradingDeals_Empty() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        var deals = tradingRepository.getAllTradingDeals();

        assertNotNull(deals);
        assertTrue(deals.isEmpty());
    }

    @Test
    void testDeleteTradingDeal_ValidDeal() throws SQLException {
        String dealID = "validDeal";

        tradingRepository.deleteTradingDeal(dealID);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteTradingDeal_InvalidDeal() throws SQLException {
        String dealID = "invalidDeal";
        doThrow(new SQLException()).when(preparedStatement).executeUpdate();

        assertThrows(RuntimeException.class, () -> tradingRepository.deleteTradingDeal(dealID));
    }
}
