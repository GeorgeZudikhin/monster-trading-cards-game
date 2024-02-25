package repository;

import model.TradingDealModel;

import java.util.List;

public interface TradingRepository {
    void createTradingDeal(int userID, TradingDealModel tradingDeal);
    List<TradingDealModel> getAllTradingDeals();
    void deleteTradingDeal(String dealID);

    boolean checkIfTradingDealExists(String tradingDealId);
}
