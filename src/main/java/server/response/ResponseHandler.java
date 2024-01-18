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
            bufferedWriter.write("SERVER.Server: Java Server example" + LINE_END);
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

    public void replyWithStatus(Object object, int statusCode, String statusMessage) {
        try {
            final String output = objectMapper.writeValueAsString(object);
            bufferedWriter.write("HTTP/1.1 " + statusCode + " " + statusMessage + LINE_END);
            bufferedWriter.write("SERVER.Server: Java Server example" + LINE_END);
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

    // Convenience method to reply with a 201 Created status
    public void replyCreated(Object object) {
        replyWithStatus(object, 201, "Created");
    }

    // Convenience method to reply with a 409 Conflict status
    public void replyConflict(Object object) {
        replyWithStatus(object, 409, "Conflict");
    }

    public void replySuccessfulLogin(Object object) {
        replyWithStatus(object, 200, "Successful login");
    }

    public void replyUnauthorized(Object object) {
        replyWithStatus(object, 401, "Unauthorized");
    }
}
