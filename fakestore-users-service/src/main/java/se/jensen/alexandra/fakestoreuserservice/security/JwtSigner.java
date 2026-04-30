package se.jensen.alexandra.fakestoreuserservice.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtSigner {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    @Value("${jwt.issuer}")
    private String issuer;

    public JwtSigner(@Value("${JWT_PRIVATE_KEY}") String privatePem, @Value("${JWT_PUBLIC_KEY}") String publicPem) throws Exception {
        String base64private = privatePem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        String base64public = publicPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] privateBytes = Base64.getDecoder().decode(base64private);
        byte[] publicBytes = Base64.getDecoder().decode(base64public);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
    }

    public String Sign(String subject, String scope) {
        Instant now = Instant.now();

        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claim("scope", scope)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(300))) // 5 minutes
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public String getIssuer() {
        return issuer;
    }
}
