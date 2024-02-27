package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class BaseController {
    protected ObjectMapper objectMapper = new ObjectMapper();

    protected <T> T parseRequestBody(String requestBody, Class<T> valueType) throws IOException {
        return objectMapper.readValue(requestBody, valueType);
    }

    // For generic collections
    protected <T> T parseRequestBody(String requestBody, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(requestBody, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

