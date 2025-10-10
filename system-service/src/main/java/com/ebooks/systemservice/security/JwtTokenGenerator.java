package com.ebooks.systemservice.security;

import com.ebooks.systemservice.entities.AccessGroupRoleMap;
import com.ebooks.systemservice.entities.SystemUser;
import com.ebooks.systemservice.repositories.AccessGroupRoleMapRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenGenerator {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenGenerator.class);

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

//        if (user.getBank() != null) {
//            claims.put("bankId", user.getBank().getId());
//            claims.put("bankName", user.getBank().getName());
//        }

        List<String> roles = getRolesFromUser(user);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
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
            return List.of();
        }
    }
}