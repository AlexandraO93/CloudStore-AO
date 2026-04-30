package se.jensen.alexandra.fakestoreuserservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import se.jensen.alexandra.fakestoreuserservice.security.JwtSigner;

import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtSigner jwtSigner;

    public TokenService(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    public String generateToken(Authentication authentication) {
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        return jwtSigner.Sign(authentication.getName(), scope);
    }
}
