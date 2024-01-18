package server.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseModel {
    private String message;
    private int statusCode;

    public ResponseModel(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}

