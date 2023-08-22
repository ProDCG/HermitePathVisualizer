package Source;

import java.util.ArrayList;

public class HermitePath {
    public HermiteInterpolator interpolator;
    private ArrayList<HermitePose> controlPoses = new ArrayList<>();

    public HermitePath() {
        interpolator = new HermiteInterpolator();
    }

    public HermitePath addPose(double x, double y, Vector2D tangent) {
        this.addPose(new HermitePose(x, y, tangent));
        return this;
    }

    public HermitePath addPose(double x, double y, double h, double m) {
        return this.addPose(new HermitePose(x, y, Vector2D.fromHeadingAndMagnitude(h, m)));
    }

    public HermitePath addPose(HermitePose pose) {
        this.controlPoses.add(pose);
        return this;
    }

    public HermitePath construct() {
        interpolator = new HermiteInterpolator();
        interpolator.setControlPoses(controlPoses);
        return this;
    }

    public Pose get(double t) {
        return interpolator.get(t);
    }

    public int length() {
        return controlPoses.size();
    }
}