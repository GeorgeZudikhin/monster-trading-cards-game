package http.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestUtility {
    private static final int offset = 0;

    public static String readHttpPath(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    public static HeaderParser parseHeaders(BufferedReader bufferedReader) throws IOException {
        HeaderParser headerParser = new HeaderParser();
        while (bufferedReader.ready()) {
            final String input = bufferedReader.readLine();
            if ("".equals(input)) {
                break;
            }
            headerParser.parseHeader(input);
        }
        return headerParser;
    }

    public static String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] charBuffer = new char[contentLength];
        int totalCharsRead = 0;
        int charsRead;

        while (totalCharsRead < contentLength && (charsRead = bufferedReader.read(charBuffer, totalCharsRead, contentLength - totalCharsRead)) != -1) {
            totalCharsRead += charsRead;
        }

        if (totalCharsRead != contentLength) {
            throw new IOException("Incomplete read: expected " + contentLength + " characters, but read only " + totalCharsRead);
        }

        return new String(charBuffer, offset, totalCharsRead);
    }
}
