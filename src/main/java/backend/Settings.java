package backend;

import java.util.HashSet;
import java.util.List;

/**
 * Class storing current settings for parsing grid file
 */
public class Settings {
    private HashSet<Short> allowedPhases;
    private Short phaseZams;
    private boolean isDefault;

    public Settings(HashSet<Short> allowedPhases, Short phaseZams, boolean isDefault) {
        this.allowedPhases = allowedPhases;
        this.phaseZams = phaseZams;
        this.isDefault = isDefault;
    }

    public Settings() {

    }

    /**
     * Phases to be used from grid file
     */
    public HashSet<Short> getPhases() {
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

    /**
     * Set settings for default grid file
     */
    public void setDefaultSettings() {
        phaseZams = 5;
        isDefault = true;
        allowedPhases = new HashSet<>(List.of((short)4, (short)5, (short)6, (short)7, (short)8, (short)9, (short)10, (short)11));
    }
}
