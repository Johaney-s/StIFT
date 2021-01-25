package backend;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class of ZAMS stars and the phase identifier
 */
public class ZAMS {
    private double zams_phase;
    private final ArrayList<Star> zams_track = new ArrayList<>();

    public void add(Star star) {
        zams_track.add(star);
        zams_track.sort(Comparator.comparing(Star::getLuminosity));
    }

    public ArrayList<Star> getTrack() {
        return zams_track;
    }

    public void set_phase(double phase) {
        zams_phase = phase;
    }

    public double get_phase() {
        return zams_phase;
    }
}
