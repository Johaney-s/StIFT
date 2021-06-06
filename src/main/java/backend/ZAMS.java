package backend;

import backend.objects.Star;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class of ZAMS stars and the phase identifier
 */
public class ZAMS {
    private Short zamsPhase;
    private final ArrayList<Star> zamsTrack = new ArrayList<>();

    public void add(Star star) {
        zamsTrack.add(star);
        zamsTrack.sort(Comparator.comparing(Star::getLuminosity));
    }

    public ArrayList<Star> getTrack() {
        return zamsTrack;
    }

    public void setPhase(Short phase) {
        zamsPhase = phase;
    }

    public double getPhase() {
        return zamsPhase;
    }
}
