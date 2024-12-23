package com.ai.AVAI.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

public class JwtUtil {

    private static final String SECRET_KEY = "NTNBNzNFNUYxQzRFMEEyRDNCNUYyRDc4NEU2QTFCNDIzRDZGMjQ3RDFGNkU1QzNBNTk2RDYzNUE3NTMyNzg1NXNvbWUtc2FsdC12YWx1ZQ==";

    public static Claims decodeJWT(String token) {
        Key key = getSigningKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
