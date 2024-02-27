package controller;

import http.request.HeaderParser;
import http.response.ResponseModel;
import http.response.ResponseWriter;
import model.UserModel;
import service.UserService;

import java.io.IOException;

public class UserController extends BaseController {
    private final UserService userService;
    private final ResponseWriter responseWriter;
    private final HeaderParser headerParser;

    public UserController(UserService userService, ResponseWriter responseWriter, HeaderParser headerParser) {
        this.userService = userService;
        this.responseWriter = responseWriter;
        this.headerParser = headerParser;
    }

    public void handleSignUp(String requestBody) throws IOException {
        final UserModel userModel = parseRequestBody(requestBody, UserModel.class);
        ResponseModel responseModel = userService.signUpUser(userModel.getUsername(), userModel.getPassword());

        switch (responseModel.getStatusCode()) {
            case 201 -> responseWriter.replyCreated(responseModel);
            case 409 -> responseWriter.replyConflict(responseModel);
        }
    }

    public void handleLogin(String requestBody) throws IOException {
        final UserModel userModel = parseRequestBody(requestBody, UserModel.class);
        ResponseModel responseModel = userService.logInUser(userModel.getUsername(), userModel.getPassword());

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
        }
    }

    public void handleUserRetrieval(String requestedUsername) {
        ResponseModel responseModel = userService.getUserProfileByUsername(headerParser.getHeader("Authorization"), requestedUsername);

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 400 -> responseWriter.replyBadRequest(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 403 -> responseWriter.replyForbidden(responseModel);
            case 404 -> responseWriter.replyNotFound(responseModel);
        }
    }

    public void handleUserUpdate(String requestBody, String requestedUsername) throws IOException {
        final UserModel userModel = parseRequestBody(requestBody, UserModel.class);
        ResponseModel responseModel = userService.updateUserProfile(headerParser.getHeader("Authorization"), requestedUsername, userModel.getNewUsername(), userModel.getNewBio(), userModel.getNewImage());

        switch (responseModel.getStatusCode()) {
            case 200 -> responseWriter.replySuccessful(responseModel);
            case 400 -> responseWriter.replyBadRequest(responseModel);
            case 401 -> responseWriter.replyUnauthorized(responseModel);
            case 403 -> responseWriter.replyForbidden(responseModel);
            case 404 -> responseWriter.replyNotFound(responseModel);
        }
    }
}
