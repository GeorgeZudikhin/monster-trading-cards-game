package repository;

import model.TradingDealModel;

import java.util.List;

public interface TradingRepository {
    void createTradingDeal(TradingDealModel tradingDeal);
    List<TradingDealModel> getAllTradingDeals();
    void deleteTradingDeal(String dealID);
}
