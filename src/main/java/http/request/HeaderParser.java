package http.request;

import java.util.HashMap;
import java.util.Map;

public class HeaderParser {
    private final Map<String, String> headersNameValue = new HashMap<>();
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String SEPARATOR = ":";

    public void parseHeader(String headerLine) {
        final String[] parts = headerLine.split(SEPARATOR, 2);
        headersNameValue.put(parts[0], parts[1].trim());
    }
    public int getContentLength() {
        final String contentLength = headersNameValue.get(CONTENT_LENGTH);
        if (contentLength == null)
            return 0;
        return Integer.parseInt(contentLength);
    }
    public String getHeader(String headerName) {
        return headersNameValue.get(headerName);
    }
}
