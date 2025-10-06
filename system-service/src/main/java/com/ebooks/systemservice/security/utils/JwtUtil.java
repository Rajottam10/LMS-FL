package com.ebooks.systemservice.security.utils;

import com.ebooks.systemservice.entities.AccessGroupRoleMap;
import com.ebooks.systemservice.entities.SystemUser;
import com.ebooks.systemservice.repositories.AccessGroupRoleMapRepository;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Autowired
    private AccessGroupRoleMapRepository accessGroupRoleMapRepository;

    public String generateToken(SystemUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("fullName", user.getName());
        claims.put("accessGroup", user.getAccessGroup().getName());

        List<String> roles = getRolesFromUser(user);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private List<String> getRolesFromUser(SystemUser user) {
        try {
            List<AccessGroupRoleMap> activeMaps = accessGroupRoleMapRepository
                    .findByAccessGroupAndIsActiveTrue(user.getAccessGroup());

            return activeMaps.stream()
                    .map(map -> map.getRole().getPermission())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("Error getting roles for user: {}", user.getUsername(), e);
            return new ArrayList<>();
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            return roles != null ? roles : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error extracting roles from token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("userId", Long.class);
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }
}