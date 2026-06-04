package se.jensen.alexandra.fakestoreuserservice.controller.instanceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.alexandra.fakestoreuserservice.util.InstanceInfo;

@RestController
public class InstanceController {
    private static final Logger log = LoggerFactory.getLogger(InstanceController.class);


    private final InstanceInfo instanceInfo;

    public InstanceController(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    @GetMapping("/whoami")
    public String whoami() {
        log.info("Received request to /whoami endpoint, returning instance information");
        return instanceInfo.toString();
    }
}
