package se.jensen.alexandra.fakestoreuserservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import se.jensen.alexandra.fakestoreuserservice.security.JwtSigner;

import java.util.stream.Collectors;

@Service
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final JwtSigner jwtSigner;

    public TokenService(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    public String generateToken(Authentication authentication) {
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        log.debug("Generating token (scope length={})", scope.length());
        return jwtSigner.Sign(authentication.getName(), scope);
    }
}
