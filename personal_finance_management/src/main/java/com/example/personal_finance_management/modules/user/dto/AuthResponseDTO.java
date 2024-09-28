package com.example.personal_finance_management.modules.user.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponseDTO (String accessToken) {
        this.accessToken = accessToken;
    }
}
