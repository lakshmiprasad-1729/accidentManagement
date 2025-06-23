package com.vsp.accidentManagement.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTutil {
    private String Secret = "7K8L9M2N5O6P3Q4R7S8T1U2V5W6X9Y0Z3A4B7C8D1E2F5G6H9I0J3K4L7M8N1O2P";
    private int jwtexp = 86400000;

//    public JWTutil() {
//        try{
//            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
//            Key sk = keygen.generateKey();
//            this.Secret = Base64.getEncoder().encodeToString(sk.getEncoded());
//            System.out.println(this.Secret);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode((this.Secret));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email){
        Map<String,Object> claims = new HashMap<>();
        return createToken(email,claims);
    }

    private String createToken(String email,Map<String, Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtexp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token,String name){
        String email = getemailFromToken(token);
        return email.equals(name) && !isTokenExp(token);
    }

    public boolean isTokenExp(String token) {
        Date expDate = getExpDatefromToken(token);
        return expDate.before(new Date());
    }

    public String getemailFromToken(String token) {
//        System.out.println("getemailFromToken");
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpDatefromToken(String token) {
        return getClaimFromToken(token,Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
