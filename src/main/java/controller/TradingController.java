package controller;

import http.request.HeaderParser;
import http.response.ResponseModel;
import http.response.ResponseWriter;
import model.TradingDealModel;
import service.TradingService;

import java.io.IOException;

public class TradingController extends BaseController {
    private final TradingService tradingService;
    private final ResponseWriter responseWriter;
    private final HeaderParser headerParser;

    public TradingController(TradingService tradingService, ResponseWriter responseWriter, HeaderParser headerParser) {
        this.tradingService = tradingService;
        this.responseWriter = responseWriter;
        this.headerParser = headerParser;
    }


    public void handleTradingDealsRetrieval() {
        ResponseModel responseModel = tradingService.getTradingDeals(headerParser.getHeader("Authorization"));

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 404 -> responseWriter.replyNotFound(responseModel);
        }
    }

    public void handleTradingDealCreation(String requestBody) throws IOException {
        final TradingDealModel tradingDeal = parseRequestBody(requestBody, TradingDealModel.class);
        ResponseModel responseModel = tradingService.createTradingDeal(headerParser.getHeader("Authorization"), tradingDeal);

        switch (responseModel.getStatusCode()) {
            case 201 -> responseWriter.replyCreated(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 409 -> responseWriter.replyConflict(responseModel);
        }
    }

    public void handleTradingDealDeletion(String dealID) {
        ResponseModel responseModel = tradingService.deleteTradingDeal(headerParser.getHeader("Authorization"), dealID);
        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
        }
    }

    public void handleTradingDealAcceptance(String requestBody, String dealID) throws IOException {
        final String cardID = parseRequestBody(requestBody, String.class);
        ResponseModel responseModel = tradingService.acceptTradingDeal(headerParser.getHeader("Authorization"), dealID, cardID);

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 400 -> responseWriter.replyBadRequest(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 403 -> responseWriter.replyForbidden(responseModel);
            case 404 -> responseWriter.replyNotFound(responseModel);
        }
    }
}
