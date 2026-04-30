package se.jensen.alexandra.fakestoreuserservice.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.alexandra.fakestoreuserservice.security.JwtSigner;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
public class JwksController {
    private final RSAPublicKey publicKey;

    public JwksController(JwtSigner jwtSigner) {
        this.publicKey = (RSAPublicKey) jwtSigner.getPublicKey();
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID("main-app-key-1")
                .build();
        return new JWKSet(jwk).toJSONObject();
    }
}
