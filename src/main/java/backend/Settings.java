package backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Class storing current settings for parsing grid file
 */
public class Settings {
    private List<Short> allowedPhases;
    private Short phaseZams;
    private boolean isDefault;

    public Settings(List<Short> allowedPhases, Short phaseZams, boolean isDefault) {
        this.allowedPhases = allowedPhases;
        this.phaseZams = phaseZams;
        this.isDefault = isDefault;
    }

    public Settings() {

    }

    /**
     * Phases to be used from grid file
     */
    public List<Short> getPhases() {
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
        allowedPhases = List.of((short)5, (short)6, (short)7, (short)8, (short)9, (short)10, (short)11);
    }
}
