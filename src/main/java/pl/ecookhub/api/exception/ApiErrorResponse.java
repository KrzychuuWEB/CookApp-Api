package pl.ecookhub.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiErrorResponse {
    private final LocalDateTime time;
    private final HttpStatus code;
    private final String message;
    private final String path;

    public ApiErrorResponse(HttpStatus code, String message, String path) {
        this.time = LocalDateTime.now();
        this.code = code;
        this.message = message;
        this.path = path;
    }
}
