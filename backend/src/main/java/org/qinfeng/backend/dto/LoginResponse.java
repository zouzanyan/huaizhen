package org.qinfeng.backend.dto;

public record LoginResponse(
    String token,
    String message,
    String username
) {}