package controller;

import http.request.HeaderParser;
import http.response.ResponseModel;
import http.response.ResponseWriter;
import service.BattleService;

public class BattleController extends BaseController {
    private final BattleService battleService;
    private final ResponseWriter responseWriter;
    private final HeaderParser headerParser;

    public BattleController(BattleService battleService, ResponseWriter responseWriter, HeaderParser headerParser) {
        this.battleService = battleService;
        this.responseWriter = responseWriter;
        this.headerParser = headerParser;
    }

    public void handleUserStatsRetrieval() {
        ResponseModel responseModel = battleService.returnUserStats(headerParser.getHeader("Authorization"));

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 404 -> responseWriter.replyNotFound(responseModel);
        }
    }

    public void handleScoreboardRetrieval() {
        ResponseModel responseModel = battleService.returnScoreboard(headerParser.getHeader("Authorization"));

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 404 -> responseWriter.replyNotFound(responseModel);
        }
    }

    public void handleBattleStart() {
        ResponseModel responseModel = battleService.startBattle(headerParser.getHeader("Authorization"));

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 403 -> responseWriter.replyForbidden(responseModel);
        }
    }

}
