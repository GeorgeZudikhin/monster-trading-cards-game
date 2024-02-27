package http.response;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ResponseWriter {

    private final BufferedWriter bufferedWriter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }

    public void replyWithStatus(Object object, int statusCode, String statusMessage) {
        try {
            final String jsonResponse = objectMapper.writeValueAsString(object) + "\n";
            bufferedWriter.write("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
            bufferedWriter.write("Server: MTCG/1.0.0" + "\r\n");
            bufferedWriter.write("Location: http://127.0.0.1:8080/" + "\r\n");
            bufferedWriter.write("Date: " + getCurrentTime() + "\r\n");
            bufferedWriter.write("Content-Type: application/json" + "\r\n");
            bufferedWriter.write("Content-Length: " + jsonResponse.length() + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.write(jsonResponse + "\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replyInPlainText(ResponseModel responseModel, String plainTextResponse) {
        try {
            bufferedWriter.write("HTTP/1.1 " + responseModel.getStatusCode() + " " + responseModel.getMessage() + "\r\n");
            bufferedWriter.write("Server: MTCG/1.0.0" + "\r\n");
            bufferedWriter.write("Location: http://127.0.0.1:8080/" + "\r\n");
            bufferedWriter.write("Date: " + getCurrentTime() + "\r\n");
            bufferedWriter.write("Content-Type: text/plain" + "\r\n");
            bufferedWriter.write("Content-Length: " + plainTextResponse.length() + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.write(plainTextResponse + "\r\n");
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

    public void replyInternalServerError(Object object) {replyWithStatus(object, 500, "Internal Server Error"); }

    public void closeConnection() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
