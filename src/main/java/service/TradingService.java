package service;

import http.response.ResponseModel;
import model.TradingDealModel;
import repository.CardRepository;
import repository.TradingRepository;
import repository.UserRepository;

import java.util.List;

public class TradingService {
    private final TradingRepository tradingRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public TradingService(TradingRepository tradingRepository, UserRepository userRepository, CardRepository cardRepository) {
        this.tradingRepository = tradingRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public ResponseModel createTradingDeal(String authToken, TradingDealModel tradingDeal) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        if(tradingRepository.checkIfTradingDealExists(tradingDeal.getId()))
            return new ResponseModel("A deal with this deal ID already exists", 409);

        tradingRepository.createTradingDeal(userID, tradingDeal);
        return new ResponseModel("Trading deal successfully created", 201);
    }

    public ResponseModel getTradingDeals(String authToken) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        List<TradingDealModel> tradingDeals = tradingRepository.getAllTradingDeals();
        if(tradingDeals.isEmpty()) {
            return new ResponseModel("The request was fine, but there are no trading deals available", 404);
        }
        return new ResponseModel("The trading deals could be retrieved successfully", 200, tradingDeals);
    }

    public ResponseModel acceptTradingDeal(String authToken, String dealId, String cardId) {
        int userId = userRepository.returnUserIDFromToken(authToken);
        if (userId == 0)
            return new ResponseModel("Invalid authentication token", 401);

        TradingDealModel deal = tradingRepository.getTradingDealById(dealId);
        if (deal == null)
            return new ResponseModel("The provided deal not found", 404);

        if (deal.getUserId() == userId)
            return new ResponseModel("Cannot trade with yourself", 400);

        if (!cardRepository.isCardEligibleForTrading(cardId, deal.getType(), deal.getMinimumDamage()))
            return new ResponseModel("Offered card does not meet the deal requirements", 400);

        boolean ownershipUpdated = cardRepository.updateCardOwnership(deal.getCardToTrade(), userId);
        if (!ownershipUpdated)
            return new ResponseModel("Failed to update card ownership", 500);

        tradingRepository.deleteTradingDeal(dealId);
        return new ResponseModel("Trade accepted successfully", 200);
    }

    public ResponseModel deleteTradingDeal(String authToken, String dealID) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        tradingRepository.deleteTradingDeal(dealID);
        return new ResponseModel("Trading deal successfully deleted", 200);
    }
}
