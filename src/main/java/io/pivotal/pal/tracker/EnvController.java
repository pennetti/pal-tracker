package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EnvController {
    private final String port;
    private final String memoryLimit;
    private final String instanceIndex;
    private final String instanceAddress;

    public EnvController(
            @Value("${port:NOT SET}") final String port,
            @Value("${memory.limit:NOT SET}") final String memoryLimit,
            @Value("${cf.instance.index:NOT SET}") final String instanceIndex,
            @Value("${cf.instance.addr:NOT SET}") final String instanceAddress
    ) {
        this.port = port;
        this.memoryLimit = memoryLimit;
        this.instanceIndex = instanceIndex;
        this.instanceAddress = instanceAddress;
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return Map.ofEntries(
                Map.entry("PORT", this.port),
                Map.entry("MEMORY_LIMIT", this.memoryLimit),
                Map.entry("CF_INSTANCE_INDEX", this.instanceIndex),
                Map.entry("CF_INSTANCE_ADDR", this.instanceAddress)
        );
    }
}
