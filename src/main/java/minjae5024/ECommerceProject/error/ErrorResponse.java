package minjae5024.ECommerceProject.error;

import java.time.OffsetDateTime;

public record ErrorResponse(String code, String message, String path, OffsetDateTime timestamp) {
    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, path, OffsetDateTime.now());
    }
}