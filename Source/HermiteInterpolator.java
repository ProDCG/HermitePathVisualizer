package Source;

import java.util.ArrayList;

import org.ejml.data.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;

public class HermiteInterpolator {
    private ArrayList<Spline> splines = new ArrayList<>();
    private ArrayList<HermitePose> controlPoses = new ArrayList<>();

    private final double[][] hermiteConstants = {
        {1.0, 0.0, 0.0, 0.0},
        {1.0, 1.0, 1.0, 1.0},
        {0.0, 1.0, 0.0, 0.0},
        {0.0, 1.0, 2.0, 3.0}
    };

    public HermiteInterpolator(HermitePose... controlPoses) {
        interpolate();
    }

    private Spline createSpline(HermitePose start, HermitePose end) {
        SimpleMatrix CUBIC_HERMITE_MATRIX = new SimpleMatrix(hermiteConstants);

        double[][] inputs = {
            {start.x, start.y},
            {end.x, end.y},
            {(1 / end.subt(start).x) * start.tangent.x, (1 / end.subt(start).y) * start.tangent.y},
            {(1 / end.subt(start).x) * end.tangent.x, (1 / end.subt(start).y) * end.tangent.y}
        };

        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputs[i].length; j++) {
                if (Double.isNaN(inputs[i][j])) {
                    inputs[i][j] = 0d;
                }
            }
        }

        try {
            SimpleMatrix O = CUBIC_HERMITE_MATRIX.solve(new SimpleMatrix(inputs));

            SimpleMatrix x = O.extractVector(false, 0);
            SimpleMatrix y = O.extractVector(false, 1);

            return new Spline(new Polynomial(x), new Polynomial(y));
        } catch(SingularMatrixException e) {
            return new Spline(null, null);
        }
    }

    private void interpolate() {
        for (int i = 0; i < controlPoses.size() - 1; i++) {
            splines.add(createSpline(controlPoses.get(i), controlPoses.get(i + 1)));
        }
    }

    public void setControlPoses(ArrayList<HermitePose> controlPoses) {
        this.controlPoses = controlPoses;
        interpolate();
    }

    public Pose get(double t, int n) {
        int splineIndex = (int) Math.floor(t);
        double splineT = t - splineIndex;
        try {
            Point point = splines.get(splineIndex).calculate(splineT, n);
            return new Pose(point.getX(), point.getY(), splines.get(splineIndex).getHeading(splineT));
        } catch (IndexOutOfBoundsException e) {
            Point point = splines.get(splines.size() - 1).calculate(1, n);
            return new Pose(point.getX(), point.getY(), splines.get(splines.size() - 1).getHeading(splineT));
        }
    }

    public double curvature(double t) {
        return splines.get(getSplineIndex(t)).curvature(t);
    }

    private int getSplineIndex(double t) {
        int splineIndex = (int) Math.floor(t);
        double splineT = t - splineIndex;
        return (int) Math.max(0.0, Math.min(splineT, controlPoses.size() - 1));
    }

    public double headingInterpolator(double initialHeading, double finalHeading, double t) {
        double deltaHeading = finalHeading - initialHeading;
        
        if (deltaHeading > 180) {
            deltaHeading -= 360;
        } else if (deltaHeading < -180) {
            deltaHeading += 360;
        }

        return (initialHeading + deltaHeading * t) % 360;
    }

}