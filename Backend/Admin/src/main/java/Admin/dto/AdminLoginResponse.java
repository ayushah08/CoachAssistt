package Admin.dto;

public record AdminLoginResponse(String message, String token, Long userId, String role) {}
