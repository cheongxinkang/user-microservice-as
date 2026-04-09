package com.xk.user.service;

import com.xk.user.dto.TokenRequest;
import com.xk.user.dto.TokenResponse;

public interface JWTService {
    TokenResponse getJWTToken(TokenRequest tokenRequest, String scope, String userId);
}
