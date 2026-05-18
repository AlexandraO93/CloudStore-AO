package se.jensen.alexandra.fakestoreuserservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import se.jensen.alexandra.fakestoreuserservice.security.JwtSigner;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtSigner jwtSigner;

    public SecurityConfig(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        //Öppnar upp för token, och att kunna registrera sig
                        .requestMatchers(HttpMethod.POST, "/users/register/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/request-token/**").permitAll()
                        .requestMatchers("/products/**").authenticated()
                        .requestMatchers("/.well-known/jwks.json").permitAll()

                        //Alla andra enpoints kräver autentisering
                        .anyRequest().authenticated()
                )
                //Tjänsten kräver giltig JWT, använder den tvättade nyckeln från JwtSigner
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(jwtSigner.getPublicKey()).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Tillåt din React-apps URL (Vites standardport är 5173)
        configuration.setAllowedOrigins(List.of("http://localhost:5173",
                "http://13.61.35.20"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
