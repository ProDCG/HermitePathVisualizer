package Source;

public class MathUtils {
    public static double clamp(double num, double min, double max) {
        return Math.max(min, Math.min(num, max));
    }
}
