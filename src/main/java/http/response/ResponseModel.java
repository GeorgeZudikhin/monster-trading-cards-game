package http.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseModel {
    private String message;
    private int statusCode;
    private Object responseBody;

    public ResponseModel(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public ResponseModel(String message, int statusCode, Object responseBody) {
        this.message = message;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}

