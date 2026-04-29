package com.levelup.backend.dto;

import java.util.UUID;

public record AuthResponse(
    String token,
    String username,
    UUID userId,
    String role
) {}
