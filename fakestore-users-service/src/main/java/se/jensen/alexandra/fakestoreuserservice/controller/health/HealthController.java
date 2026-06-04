package se.jensen.alexandra.fakestoreuserservice.controller.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.alexandra.fakestoreuserservice.repository.UserRepository;
import se.jensen.alexandra.fakestoreuserservice.util.InstanceInfo;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final UserRepository userRepository;
    private final InstanceInfo instanceInfo;


    public HealthController(UserRepository userRepository, InstanceInfo instanceInfo) {
        this.userRepository = userRepository;
        this.instanceInfo = instanceInfo;
    }

    @GetMapping("/live")
    public Map<String, Object> live() {
        log.info("Health check (live) requested");
        return Map.of(
                "status", "UP",
                "instance", instanceInfo.getInstanceName(),
                "time", Instant.now().toString()
        );
    }

    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        try {
            long userCount = userRepository.count();

            return ResponseEntity.ok(Map.of(
                    "status", "READY",
                    "database", "UP",
                    "users", userCount,
                    "instance", instanceInfo.getInstanceName(),
                    "time", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "status", "NOT_READY",
                    "database", "DOWN",
                    "instance", instanceInfo.getInstanceName(),
                    "time", Instant.now().toString()
            ));
        }
    }
}
