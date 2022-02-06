package pl.ecookhub.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class AbstractApiController {

    public ResponseEntity<Object> jsonResponse(String message, Object data, HttpStatus status) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("message", message);
        response.put("status", status.value());
        response.put("data", data);

        return new ResponseEntity<>(response, status);
    }
}
