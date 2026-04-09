package com.xk.user.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.xk.user.dto.TokenRequest;
import com.xk.user.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService{

    @Value("security.rsa.private")
    private String privateRSAKey;
    @Value("security.rsa.public")
    private String publicRSAKey;
    @Value("security.rsa.keyId")
    private String keyId;


    @Override
    public TokenResponse getJWTToken(TokenRequest tokenRequest, String scope, String userId) {
        try {
            KeyPair keyPair = loadRsaKey();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            Date issueTime = new Date();
            Date expiry = new Date(System.currentTimeMillis() + 3600 * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issueTime(issueTime)
                    .claim("scope", scope)
                    .expirationTime(expiry) // 1 hour expiry
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(this.keyId).build(),
                    claimsSet
            );

            signedJWT.sign(new RSASSASigner(privateKey));
            String jwtToken = signedJWT.serialize();
            return new TokenResponse(jwtToken, "Bearer", "3600", scope);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating token", ex);
        }
    }

    private KeyPair loadRsaKey() {
        byte[] privateKeyBytes = Base64.getDecoder().decode(this.privateRSAKey);
        byte[] publicKeyBytes = Base64.getDecoder().decode(this.publicRSAKey);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        PrivateKey privateKey = null;
        try {
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = null;
        try {
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return new KeyPair(publicKey, privateKey);
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

            System.out.println("Private key: " + privateKeyString);
            System.out.println("Public key: " + publicKeyString);
            return keyPair;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
