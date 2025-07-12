package org.zarhub.ProfileService;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ProfileService {
    private final Environment environment;

    public ProfileService(Environment environment) {
        this.environment = environment;
    }

    public boolean isDebugMode() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }
}
