package org.qinfeng.admin.dto;

public record LoginResponse(
    String token,
    String message,
    String username
) {}