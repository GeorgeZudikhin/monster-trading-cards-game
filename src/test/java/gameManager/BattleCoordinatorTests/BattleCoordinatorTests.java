package gameManager.BattleCoordinatorTests;

import gameElements.card.Card;
import gameElements.card.CardElement;
import gameElements.card.CardType;
import gameElements.card.MonsterCard;
import gameElements.user.User;
import gameManager.BattleCoordinator;
import gameManager.BattleLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BattleCoordinatorTests {
    @Mock
    private BattleLogger mockBattleLogger;

    private User playerOne;
    private User playerTwo;
    private List<Card> playerOneCards;
    private List<Card> playerTwoCards;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerOne = new User("PlayerOne");
        playerTwo = new User("PlayerTwo");

        Card playerOneCard = new MonsterCard("1", CardType.DRAGON, 50, CardElement.FIRE);
        Card playerTwoCard = new MonsterCard("2", CardType.GOBLIN, 30, CardElement.NORMAL);

        playerOneCards = new ArrayList<>(List.of(playerOneCard));
        playerTwoCards = new ArrayList<>(List.of(playerTwoCard));
    }

    @Test
    public void testBattleRoundLogic() {
        BattleCoordinator coordinator = new BattleCoordinator(playerOne, playerOneCards, playerTwo, playerTwoCards, mockBattleLogger);
        coordinator.startBattle();

        verify(mockBattleLogger, atLeastOnce()).printRoundStart(anyInt(), any());
        verify(mockBattleLogger, atLeastOnce()).printBattleEnd();
    }

    @Test
    public void testPlayerOneWinsRound() {
        Card playerOneCard = new MonsterCard("1", CardType.DRAGON, 50, CardElement.FIRE);
        Card playerTwoCard = new MonsterCard("2", CardType.GOBLIN, 20, CardElement.NORMAL);

        playerOneCards.set(0, playerOneCard);
        playerTwoCards.set(0, playerTwoCard);

        BattleCoordinator coordinator = new BattleCoordinator(playerOne, playerOneCards, playerTwo, playerTwoCards, mockBattleLogger);
        coordinator.startBattle();

        verify(mockBattleLogger).printRoundWinner(playerOne);
    }

    @Test
    public void testPlayerTwoWinsRound() {
        Card playerOneCard = new MonsterCard("1", CardType.GOBLIN, 20, CardElement.NORMAL);
        Card playerTwoCard = new MonsterCard("2", CardType.DRAGON, 50, CardElement.FIRE);

        playerOneCards.set(0, playerOneCard);
        playerTwoCards.set(0, playerTwoCard);

        BattleCoordinator coordinator = new BattleCoordinator(playerOne, playerOneCards, playerTwo, playerTwoCards, mockBattleLogger);
        coordinator.startBattle();

        verify(mockBattleLogger).printRoundWinner(playerTwo);
    }

    @Test
    public void testDrawRound() {
        Card playerOneCard = new MonsterCard("1", CardType.DRAGON, 30, CardElement.FIRE);
        Card playerTwoCard = new MonsterCard("2", CardType.DRAGON, 30, CardElement.FIRE);

        playerOneCards.set(0, playerOneCard);
        playerTwoCards.set(0, playerTwoCard);

        BattleCoordinator coordinator = new BattleCoordinator(playerOne, playerOneCards, playerTwo, playerTwoCards, mockBattleLogger);
        coordinator.startBattle();

        verify(mockBattleLogger, times(100)).printRoundResultDraw();
    }

    @Test
    public void testCardTransferOnWin() {
        Card playerOneCard = new MonsterCard("1", CardType.DRAGON, 50, CardElement.FIRE);
        Card playerTwoCard = new MonsterCard("2", CardType.GOBLIN, 20, CardElement.NORMAL);

        playerOneCards.set(0, playerOneCard);
        playerTwoCards.set(0, playerTwoCard);

        BattleCoordinator coordinator = new BattleCoordinator(playerOne, playerOneCards, playerTwo, playerTwoCards, mockBattleLogger);
        coordinator.startBattle();

        assertTrue(playerOneCards.contains(playerTwoCard), "Player One's deck should contain Player Two's card after winning.");
    }
}
