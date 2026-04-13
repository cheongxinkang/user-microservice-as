package com.xk.user.service;

import org.springframework.stereotype.Service;

@Service
public class ScopeServiceImpl implements ScopeService {

    @Override
    public String findScope(String userType) {

        if ("admin".equals(userType)) {
            return "achievement_system:write";
        }

        if ("cultivator".equals(userType)) {
            return "achievement_system:read";
        }

        return null;
    }

}
