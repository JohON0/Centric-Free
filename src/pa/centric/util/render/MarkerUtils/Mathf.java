package pa.centric.util.render.MarkerUtils;

public class Mathf {
    public static double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }
}
