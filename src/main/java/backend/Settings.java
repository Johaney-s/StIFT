package backend;

import java.util.List;

/**
 * Class storing current settings for parsing grid file
 */
public class Settings {
    private List<Integer> allowedPhases;
    private Short phaseZams;
    private static Settings instance;
    private boolean isDefault;

    public Settings(List<Integer> allowedPhases, Short phaseZams, boolean isDefault) {
        this.allowedPhases = allowedPhases;
        this.phaseZams = phaseZams;
        this.isDefault = isDefault;
    }

    /**
     * Save new settings
     */
    public void setSettings(Settings newSettings) {
        this.instance = instance;
    }

    /**
     * Phases to be used from grid file
     */
    public List<Integer> getPhases() {
        return this.allowedPhases;
    }

    /**
     * Phase representing ZAMS (could be null!)
     */
    public Short getPhaseZams() {
        return this.phaseZams;
    }

    /**
     * True if this is the default dataset
     */
    public boolean isDefault() {
        return this.isDefault;
    }
}
