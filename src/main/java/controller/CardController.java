package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import gameElements.Card;
import http.request.HeaderParser;
import http.response.ResponseModel;
import http.response.ResponseWriter;
import model.CardModel;
import service.CardService;
import service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class CardController extends BaseController {
    private final CardService cardService;
    private final UserService userService;
    private final ResponseWriter responseWriter;
    private final HeaderParser headerParser;

    public CardController(CardService cardService, UserService userService, ResponseWriter responseWriter, HeaderParser headerParser) {
        this.cardService = cardService;
        this.userService = userService;
        this.responseWriter = responseWriter;
        this.headerParser = headerParser;
    }

    public void handleCreationsOfPackages(String requestBody) {
        List<CardModel> cardModels = parseRequestBody(requestBody, new TypeReference<>() {});

        ResponseModel responseModel = null;
        boolean allCardsCreated = true;

        for (CardModel card : cardModels) {
            responseModel = cardService.generateNewCard(
                    card.getCardID(),
                    card.getCardName(),
                    card.getCardDamage(),
                    card.getCardElement(),
                    card.getPackageID(),
                    headerParser.getHeader("Authorization"));

            if (responseModel.getStatusCode() != 201) {
                allCardsCreated = false;
                break;
            }
        }
        if (allCardsCreated) {
            responseWriter.replyCreated(responseModel);
        } else {
            switch (responseModel.getStatusCode()) {
                case 401 -> responseWriter.replyUnauthorized(responseModel);
                case 403 -> responseWriter.replyForbidden(responseModel);
            }
        }
    }

    public void handleAcquisitionOfPackages() {
        ResponseModel responseModel = cardService.acquirePackage(headerParser.getHeader("Authorization"));

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 403 -> responseWriter.replyForbidden(responseModel);
            case 404 -> responseWriter.replyNotFound(responseModel);
        }
    }

    public void handleUserCardsRetrieval() {
        ResponseModel responseModel = userService.returnAllUserCards(headerParser.getHeader("Authorization"));

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 204 -> responseWriter.replyNoContent(responseModel);
        }
    }

    public void handleUserDeckRetrieval(String query) {
        ResponseModel responseModel = userService.returnUserDeck(headerParser.getHeader("Authorization"));

        if (responseModel.getStatusCode() == 200) {
            if (query.contains("format=plain")) {
                List<Card> deck = (List<Card>) responseModel.getResponseBody();
                String plainResponse = deck.stream()
                        .map(Card::toString)
                        .collect(Collectors.joining("\n"));

                responseWriter.replyInPlainText(responseModel, plainResponse);
            } else {
                responseWriter.replySuccessful(responseModel);
            }
        } else if (responseModel.getStatusCode() == 401) {
            responseWriter.replyUnauthorized(responseModel);
        } else if (responseModel.getStatusCode() == 404) {
            responseWriter.replyNotFound(responseModel);
        }
    }

    public void handleUserDeckUpdate(String requestBody) {
        List<String> cardIDs = parseRequestBody(requestBody, new TypeReference<>() {});
        ResponseModel responseModel = null;
        boolean forbidden = false;

        if(cardIDs.size() < 4) {
            responseWriter.replyBadRequest(new ResponseModel("The provided deck did not include the required amount of cards", 400));
        } else {
            for (String cardID : cardIDs) {
                responseModel = userService.addCardToDeck(headerParser.getHeader("Authorization"), cardID);
                if (responseModel.getStatusCode() == 403) {
                    forbidden = true;
                    break;
                }
            }
            if (forbidden) {
                responseWriter.replyForbidden(responseModel);
            } else if (responseModel.getStatusCode() == 200) {
                responseWriter.replySuccessful(responseModel);
            } else if (responseModel.getStatusCode() == 401) {
                responseWriter.replyUnauthorized(responseModel);
            }
        }
    }

    public void handleGambling() {
        ResponseModel responseModel = cardService.gambleCard(headerParser.getHeader("Authorization"));

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 204 -> responseWriter.replyNoContent(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 500 -> responseWriter.replyInternalServerError(responseModel);
        }
    }
}
