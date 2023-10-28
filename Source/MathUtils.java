package Source;

public class MathUtils {
    public static double clamp(double num, double min, double max) {
        return Math.max(min, Math.min(num, max));
    }

    public static boolean epsilonEquals(double val1, double val2){
        return Math.abs(val1 - val2) < 1e-6;
    }
}
