package repository.repositoryImpl;

import model.TradingDealModel;
import repository.TradingRepository;
import repository.UserRepository;

import java.util.List;

public class TradingRepositoryImpl implements TradingRepository {
    private static TradingRepositoryImpl tradingRepository;

    private TradingRepositoryImpl() {}

    public static synchronized TradingRepositoryImpl getInstance() {
        if (tradingRepository == null) {
            tradingRepository = new TradingRepositoryImpl();
        }
        return tradingRepository;
    }

    @Override
    public void createTradingDeal(TradingDealModel tradingDeal) {
        // SQL to insert a new trading deal into the TradingDeals table
    }

    @Override
    public List<TradingDealModel> getAllTradingDeals() {
        // SQL to fetch all trading deals from the TradingDeals table
    }

    @Override
    public void deleteTradingDeal(String dealID) {
        // SQL to delete a trading deal from the TradingDeals table
    }
}
