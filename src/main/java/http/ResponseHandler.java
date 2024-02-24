package http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;

public class ResponseHandler {

    private final BufferedWriter bufferedWriter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseHandler(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public void reply(Object object) {
        try {
            final String output = objectMapper.writeValueAsString(object);
            bufferedWriter.write("HTTP/1.1 200 OK" + "\r\n");
            bufferedWriter.write("SERVER.Server: Java Server" + "\r\n");
            bufferedWriter.write("Content-Type: application/json" + "\r\n");
            bufferedWriter.write("Connection: close" + "\r\n");
            bufferedWriter.write("Content-Length: " + output.length() + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.write(output + "\r\n");
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
            bufferedWriter.write("HTTP/1.1 " + responseModel.getStatusCode() + " " + responseModel.getMessage() + "\r\n");
            bufferedWriter.write("Server: MTCG Java Server" + "\r\n");
            bufferedWriter.write("Content-Type: text/plain" + "\r\n");
            bufferedWriter.write("Connection: close" + "\r\n");
            bufferedWriter.write("Content-Length: " + plainText.length() + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.write(plainText + "\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void replyWithStatus(Object object, int statusCode, String statusMessage) {
        try {
            final String output = objectMapper.writeValueAsString(object) + "\n";
            bufferedWriter.write("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
            bufferedWriter.write("Server: MTCG Java Server" + "\r\n");
            bufferedWriter.write("Content-Type: application/json" + "\r\n");
            bufferedWriter.write("Connection: close" + "\r\n");
            bufferedWriter.write("Content-Length: " + output.length() + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.write(output + "\r\n");
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
