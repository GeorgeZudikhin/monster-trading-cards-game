package service;

import http.ResponseModel;
import model.TradingDealModel;
import repository.TradingRepository;
import repository.UserRepository;

import java.util.List;

public class TradingService {
    private final TradingRepository tradingRepository;
    private final UserRepository userRepository;

    public TradingService(TradingRepository tradingRepository, UserRepository userRepository) {
        this.tradingRepository = tradingRepository;
        this.userRepository = userRepository;
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

//    public ResponseModel acceptTradingDeal(String authToken, String dealID, String cardID) {
//        // Logic for one user to accept another user's trading deal
//    }
//
    public ResponseModel deleteTradingDeal(String authToken, String dealID) {
        int userID = userRepository.returnUserIDFromToken(authToken);
        if(userID == 0)
            return new ResponseModel("Access token is missing or invalid", 401);

        tradingRepository.deleteTradingDeal(dealID);
        return new ResponseModel("Trading deal successfully deleted", 200);
    }
}
