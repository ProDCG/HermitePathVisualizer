package Source;

import java.util.Arrays;
import java.util.Comparator;

public class Test {
    Polynomial x;
    Polynomial y;
    Spline s;

    public Test(Polynomial x, Polynomial y, Spline s) {
        this.x = x;
        this.y = y;
        this.s = s;
    }

    public double gradientOf(double t, Pose targetPose) {
        double dx = x.calculate(t, 1);
        double dy = y.calculate(t, 1);
        double fx = x.calculate(t, 0) - targetPose.x;
        double fy = y.calculate(t, 0) - targetPose.y;
        return 2 * (dx * fx + dy * fy);
    }

    public double findMinimumT(Pose currentPose) {
        double currentT = 0.5;
        for (int i = 0; i < 10; i++) {
            double gradient = gradientOf(currentT, currentPose);
            double hessian = 2 * (Math.pow(s.getX().calculate(currentT, 1), 2) + Math.pow(s.getY().calculate(currentT, 1), 1));
            double newT = currentT - (gradient / hessian);

            if (Math.abs(newT - currentT) < 1e-6) {
                break;
            }
            currentT = newT;
        }

        currentT = MathUtils.clamp(currentT, 0.0, 1.0);
        Vector2D dist1 = new Vector2D(0.0, dist(0.0, currentPose));
        Vector2D dist2 = new Vector2D(currentT, dist(currentT, currentPose));
        Vector2D dist3 = new Vector2D(1.0 - 1e-6, dist(1.0 - 1e-6, currentPose));

        Vector2D[] distances = {dist1, dist2, dist3};

        Vector2D nearestDist = Arrays.stream(distances)
                                    .min(Comparator.comparing(Vector2D::getY))
                                    .orElse(null);

        // double distance = (s.calculate(currentT, 0).subt(currentPose)).toVec2D().magnitude();
        System.out.println(nearestDist.getY());
        return nearestDist.getX();
    }

    private double dist(double currentT, Pose currentPose) {
        return (s.calculate(currentT, 0).subt(currentPose)).toVec2D().magnitude();
    }
}
