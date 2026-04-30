package com.levelup.backend.dto;

public record AuthResponse(
    String token,
    String username,
    Long userId,
    String role
) {}
