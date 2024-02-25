package service;

import http.ResponseModel;
import model.TradingDealModel;
import repository.TradingRepository;

import java.util.List;

public class TradingService {
    private final TradingRepository tradingRepository;

    public TradingService(TradingRepository tradingRepository) {
        this.tradingRepository = tradingRepository;
    }

    public ResponseModel createTradingDeal(String authToken, TradingDealModel tradingDeal) {
        // Logic to validate the user and the card, then create a trading deal
    }

    public List<TradingDealModel> getTradingDeals(String authToken) {
        // Logic to fetch and return all trading deals
    }

    public ResponseModel acceptTradingDeal(String authToken, String dealID, String cardID) {
        // Logic for one user to accept another user's trading deal
    }

    public ResponseModel deleteTradingDeal(String authToken, String dealID) {
        // Logic to allow a user to delete their trading deal
    }
}
