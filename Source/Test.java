package Source;

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

    public double findT(Pose targetPose) {
        final double TOLERANCE = 1e-6;
        final int MAX_ITERATIONS = 100;

        double currentT = 0.5;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double gradient = gradientOf(currentT, targetPose);
            double hessian = 2 * (Math.pow(x.calculate(currentT, 1), 2) + Math.pow(y.calculate(currentT, 1), 2));
            double newT = currentT - (gradient / hessian);

            System.out.println(newT);
            if (Math.abs(newT - currentT) < TOLERANCE) {
                System.out.println("BREAKING: " + i);
                break;
            }
            currentT = newT;
        }

        double distance = s.calculate(currentT, 0).subt(targetPose.toPoint()).magnitude();
        System.out.println("DISTANCE: " + distance + "\nCLOSEST T: " + currentT);
        return currentT;
    }
}
