package se.jensen.alexandra.fakestoreuserservice.controller.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.alexandra.fakestoreuserservice.dto.login.LoginRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.login.LoginResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.security.MyUserDetails;
import se.jensen.alexandra.fakestoreuserservice.service.TokenService;

@RestController
@RequestMapping("/request-token")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> token(
            @RequestBody LoginRequestDTO loginRequestDTO) {

        log.info("Authentication attempt received");
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.email(),
                        loginRequestDTO.password()
                )
        );

        MyUserDetails details = (MyUserDetails) auth.getPrincipal();
        details.getId();

        String token = tokenService.generateToken(auth);

        log.info("Authentication successful for userId={}", details.getId());

        return ResponseEntity.ok(new LoginResponseDTO(token, details.getId()));
    }
}
