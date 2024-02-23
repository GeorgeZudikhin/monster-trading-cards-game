package server.response;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;

public class ResponseHandler {

    public static final String LINE_END = "\r\n";
    private final BufferedWriter bufferedWriter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseHandler(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public void reply(Object object) {
        try {
            final String output = objectMapper.writeValueAsString(object);
            bufferedWriter.write("HTTP/1.1 200 OK" + LINE_END);
            bufferedWriter.write("SERVER.Server: Java Server" + LINE_END);
            bufferedWriter.write("Content-Type: application/json" + LINE_END);
            bufferedWriter.write("Connection: close" + LINE_END);
            bufferedWriter.write("Content-Length: " + output.length() + LINE_END);
            bufferedWriter.write(LINE_END);
            bufferedWriter.write(output + LINE_END);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reply() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replyInPlainText(ResponseModel responseModel, String plainText) {
        try {
            bufferedWriter.write("HTTP/1.1 " + responseModel.getStatusCode() + " " + responseModel.getMessage() + LINE_END);
            bufferedWriter.write("Server: MCTG Java Server" + LINE_END);
            bufferedWriter.write("Content-Type: text/plain" + LINE_END);
            bufferedWriter.write("Connection: close" + LINE_END);
            bufferedWriter.write("Content-Length: " + plainText.length() + LINE_END);
            bufferedWriter.write(LINE_END);
            bufferedWriter.write(plainText + LINE_END);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void replyWithStatus(Object object, int statusCode, String statusMessage) {
        try {
            final String output = objectMapper.writeValueAsString(object) + "\n";
            bufferedWriter.write("HTTP/1.1 " + statusCode + " " + statusMessage + LINE_END);
            bufferedWriter.write("Server: MCTG Java Server" + LINE_END);
            bufferedWriter.write("Content-Type: application/json" + LINE_END);
            bufferedWriter.write("Connection: close" + LINE_END);
            bufferedWriter.write("Content-Length: " + output.length() + LINE_END);
            bufferedWriter.write(LINE_END);
            bufferedWriter.write(output + LINE_END);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replySuccessful(Object object) {
        replyWithStatus(object, 200, "Successful");
    }

    public void replyCreated(Object object) {
        replyWithStatus(object, 201, "Created");
    }

    public void replyNoContent(Object object) {
        replyWithStatus(object, 204, "No Content");
    }

    public void replyBadRequest(Object object) {
        replyWithStatus(object, 400, "Bad Request");
    }

    public void replyUnauthorized(Object object) {
        replyWithStatus(object, 401, "Unauthorized");
    }

    public void replyForbidden(Object object) {
        replyWithStatus(object, 403, "Forbidden");
    }

    public void replyNotFound(Object object) {
        replyWithStatus(object, 404, "Not Found");
    }

    public void replyConflict(Object object) {
        replyWithStatus(object, 409, "Conflict");
    }
}
