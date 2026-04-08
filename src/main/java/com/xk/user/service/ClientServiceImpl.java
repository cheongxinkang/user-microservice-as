package com.xk.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService{

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Override
    public boolean validateClient(String clientId, String clientSecret) {
        return this.clientId.equals(clientId)
            && this.clientSecret.equals(clientSecret);
    }

}
