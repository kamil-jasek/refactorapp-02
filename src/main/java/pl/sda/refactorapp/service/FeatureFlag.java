package pl.sda.refactorapp.service;

import java.util.Map;
import pl.sda.refactorapp.annotation.Service;

@Service
public final class FeatureFlag {

    private final Map<String, Boolean> flags;

    public FeatureFlag(Map<String, Boolean> flags) {
        this.flags = flags;
    }

    public boolean isEnabled(String name) {
        return false;
    }

    // enable / disable
}
