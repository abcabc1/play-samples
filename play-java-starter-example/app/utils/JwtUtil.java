package utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static String iss = "Bob";
    private static String sub = "play";
    private static String key = "111111";

    public static Claims check(String token) {
        if (token == null) {
            return null;
        }
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    public static String create(Map<String, Object> map) {
        Claims claims = Jwts.claims(map).setSubject(sub).setIssuer(iss).setIssuedAt(new Date());
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, key).setClaims(claims).compact();
    }
}
