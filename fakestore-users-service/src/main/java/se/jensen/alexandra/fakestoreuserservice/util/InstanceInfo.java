package se.jensen.alexandra.fakestoreuserservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InstanceInfo {


    private final String instanceName;

    public InstanceInfo(@Value("${app.instance.name:local}") String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() {
        return instanceName;
    }
}
