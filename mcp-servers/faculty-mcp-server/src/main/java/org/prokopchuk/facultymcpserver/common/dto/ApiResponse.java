package org.prokopchuk.facultymcpserver.common.dto;

public record ApiResponse<T>(
        int responseCode,
        String message,
        T body
) {

    public static <T> ApiResponse<T> ok(String message, T body) {
        return new ApiResponse<>(200, message, body);
    }

    public static <T> ApiResponse<T> created(String message, T body) {
        return new ApiResponse<>(201, message, body);
    }

    public static ApiResponse<Void> noContent(String message) {
        return new ApiResponse<>(204, message, null);
    }

    public static ApiResponse<Void> badRequest(String message) {
        return new ApiResponse<>(400, message, null);
    }

    public static ApiResponse<Void> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }

    public static ApiResponse<Void> conflict(String message) {
        return new ApiResponse<>(409, message, null);
    }

    public static ApiResponse<Void> internalServerError(String message) {
        return new ApiResponse<>(500, message, null);
    }

}
