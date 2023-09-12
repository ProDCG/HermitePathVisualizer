package Source;

public class GVFPathFollower {
    private HermitePath path;

    private final double MAX_VELOCITY = 90; /* Inches per second */
    private final double MAX_ACCEL = 480; /* Inches per second squared */
    private final double MAX_DECEL = 160; /* Inches per second squared */

    private final double ACCEL_PERIOD_DIST = (Math.pow(MAX_VELOCITY, 2)) / (2 * MAX_ACCEL);
    private final double DECEL_PERIOD_DIST = (Math.pow(MAX_VELOCITY, 2)) / (2 * MAX_DECEL);

    private double kN;
    private double kS;
    private Pose currentPose;

    public GVFPathFollower(HermitePath path, final Pose initialPose, double kN, double kS) {
        this.path = path;
        this.currentPose = initialPose;
        this.kN = kN;
        this.kS = kS;
    }

    public double getNearestT() {
        Vector2D nearestSplineDist = new Vector2D(0, Integer.MAX_VALUE);
        for (int i = 0; i < path.length(); i++) {
            for (int j = 1; j < 10; j++) {
                double currentT = ((double) j / 10) + i;

                double dist = currentPose.subt(path.get(currentT, 0)).toVec2D().magnitude();
                if (dist < nearestSplineDist.y) {
                    nearestSplineDist.x = currentT;
                    nearestSplineDist.y = dist;
                }
            }
        }

        double pGuess = nearestSplineDist.x;
        double startRange = Math.max(0.0, pGuess - 0.1);
        double endRange = Math.min(path.length(), pGuess + 0.1);

        Vector2D nearestSplineDist2 = new Vector2D(nearestSplineDist.x, nearestSplineDist.y);

        for (double currentT = startRange; currentT <= endRange; currentT += 0.001) {
            double dist = currentPose.subt(path.get(currentT, 0)).toVec2D().magnitude();

            if (dist < nearestSplineDist2.y) {
                nearestSplineDist.x = currentT;
                nearestSplineDist.y = dist;
            }
        }

        return nearestSplineDist2.x;
    }

    public Vector2D calculateGVF() {
        double nearestT = getNearestT();
        Vector2D tangent = path.get(nearestT, 1).toVec2D().unit();
        Vector2D normal = tangent.rotate(Math.PI / 2);
        
        Vector2D displacement = path.get(nearestT, 0).subt(currentPose).toVec2D();
        double error = displacement.magnitude() * Math.signum((displacement.cross(tangent)));
        Vector2D gvf;
        double vMax = MAX_VELOCITY;

        gvf = (tangent.subt(normal.mult(kN).mult(error < 1 ? 0 : 0))).unit();

        double accel_disp = currentPose.subt(path.startPose()).toVec2D().magnitude();
        double decel_disp = currentPose.subt(path.endPose()).toVec2D().magnitude();        

        if (decel_disp < DECEL_PERIOD_DIST) {
            return gvf.mult(MAX_VELOCITY * (decel_disp / DECEL_PERIOD_DIST)).mult(kS);
        } else if (accel_disp < ACCEL_PERIOD_DIST) {
            return gvf.mult(MAX_VELOCITY * (accel_disp / ACCEL_PERIOD_DIST)).mult(kS);
        }

        double curvature = path.curvature(nearestT);
        if (curvature != 0) {
            vMax = Math.min(Math.sqrt(MAX_ACCEL / curvature), MAX_VELOCITY);
        }
        gvf = gvf.mult(vMax).mult(kS);

        return gvf;
    }

    public double errorMap(double error) {
        return Math.max(error, 1);
    }

    public void setCurrentPose(Pose currentPose) {
        this.currentPose = currentPose;
    }

    // private double clamp(double num, double min, double max) {
    //     return Math.max(min, Math.min(num, max));
    // }
}
