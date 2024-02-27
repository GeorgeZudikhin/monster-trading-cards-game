package http.request;

import java.util.HashMap;
import java.util.Map;

public class HeaderParser {
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String SEPARATOR = ":";
    private final Map<String, String> headersNameValue = new HashMap<>();

    public void parseHeader(String headerLine) {
        final String[] parts = headerLine.split(SEPARATOR, 2);
        headersNameValue.put(parts[0], parts[1].trim());
    }
    public int getContentLength() {
        final String header = headersNameValue.get(CONTENT_LENGTH);
        if (header == null) {
            return 0;
        }
        return Integer.parseInt(header);
    }
    public String getHeader(String headerName) {
        return headersNameValue.get(headerName);
    }
}
