package Source;

public class GVFPathFollower {
    private HermitePath path;

    private final int MAX_APPROX = 5;
    private final double MAX_VELOCITY = 10; /* Inches per second */
    private final double MAX_ACCEL = 50; /* Inches per second squared */
    private final double MAX_DECEL = 50; /* Inches per second squared */
    private final double ACCEL_PERIOD_DIST = MAX_VELOCITY / MAX_ACCEL;
    private final double DECEL_PERIOD_DIST = MAX_VELOCITY / MAX_DECEL;

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
                System.out.println(currentT);

                double dist = currentPose.subt(path.get(currentT, 0)).toVec2D().magnitude();
                if (dist < nearestSplineDist.y) {
                    nearestSplineDist.x = currentT;
                    nearestSplineDist.y = dist;
                }
            }
        }

        double pGuess = nearestSplineDist.x;
        for (int i = 0; i < MAX_APPROX; i++) {
            Pose tPose = path.get(pGuess, 0);
            Vector2D pointVector = tPose.subt(currentPose).toVec2D().unit().mult(0.2);
            Vector2D tangentVector = path.get(pGuess, 1).toVec2D().unit().mult(0.2); 

            pGuess = clamp(pGuess - tangentVector.dot(pointVector), 0, path.length());
        }

        return pGuess;
    }

    public Vector2D calculateGVF() {
        double nearestT = getNearestT();
        Vector2D tangent = path.get(nearestT, 1).toVec2D().unit();
        Vector2D normal = tangent.rotate(Math.PI / 2);
        
        Vector2D displacement = path.get(nearestT, 0).subt(currentPose).toVec2D();

        double error = displacement.magnitude() * Math.signum((displacement.cross(tangent)));
        Vector2D gvf = (tangent.subt(normal.mult(kN).mult(error))).unit();
        
        double accel_disp = currentPose.subt(path.startPose()).toVec2D().magnitude();
        double decel_disp = currentPose.subt(path.endPose()).toVec2D().magnitude();
        if (accel_disp < ACCEL_PERIOD_DIST) {
            return gvf.mult(accel_disp * MAX_ACCEL);
        } else if (decel_disp < DECEL_PERIOD_DIST) {
            return gvf.mult(decel_disp * MAX_DECEL);
        } else {
            double curvature = path.curvature(nearestT);
            double vMax = Math.sqrt(MAX_ACCEL / curvature);
            return gvf.mult(vMax).mult(kS);
        }
    }

    public void setCurrentPose(Pose currentPose) {
        this.currentPose = currentPose;
    }

    private double clamp(double num, double min, double max) {
        return Math.max(min, Math.min(num, max));
    }
}
