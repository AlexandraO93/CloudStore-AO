package se.jensen.alexandra.fakestoreuserservice.controller.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.alexandra.fakestoreuserservice.security.JwtSigner;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
public class JwksController {
    private static final Logger log = LoggerFactory.getLogger(JwksController.class);
    private final RSAPublicKey publicKey;

    public JwksController(JwtSigner jwtSigner) {
        this.publicKey = (RSAPublicKey) jwtSigner.getPublicKey();
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        log.debug("JWKS endpoint requested");
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID("main-app-key-1")
                .build();
        return new JWKSet(jwk).toJSONObject();
    }
}
