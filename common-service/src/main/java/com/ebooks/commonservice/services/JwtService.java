package com.ebooks.commonservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final String secretKey = "mySuperSecretKeyThatIsAtLeast512BitsLongForHS512Algorithm1234567890abcdefghijklmnopqr";
    @Value("${spring.security.jwt.expiration:86400000}")
    private long jwtExpiration;

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap();
        claims.put("roles", Arrays.asList("GOD"));
        return Jwts.builder().claims(claims).subject(username).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + this.jwtExpiration)).signWith(this.getKey()).compact();
    }

    private Key getKey() {
        byte[] keyBytes = (byte[])Decoders.BASE64.decode("mySuperSecretKeyThatIsAtLeast512BitsLongForHS512Algorithm1234567890abcdefghijklmnopqr");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return (String)this.extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = this.extractAllClaims(token);
        return (T)claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return (Claims)Jwts.parser().verifyWith((SecretKey)this.getKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String userName = this.extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !this.isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return (Date)this.extractClaim(token, Claims::getExpiration);
    }
}
