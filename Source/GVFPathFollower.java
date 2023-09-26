package Source;

import java.util.stream.IntStream;

public class GVFPathFollower {
    private HermitePath path;

    private final double MAX_VELOCITY = 180; /* Inches per second */
    private final double MAX_ACCEL = 480; /* Inches per second squared */
    private final double MAX_DECEL = 720; /* Inches per second squared */
    private final double FINISH_TOLERANCE = 0.1; /* Finishing Error */
    private double lastVelocity = 0.5;

    // private final double ACCEL_PERIOD_DIST = (Math.pow(MAX_VELOCITY, 2)) / (2 * MAX_ACCEL);s
    private final double DECEL_PERIOD_DIST = (Math.pow(MAX_VELOCITY, 2)) / (2 * MAX_DECEL);

    private double kN;
    private double kS;
    private double kC;
    private Pose currentPose;
    public static double nearestT = 0.0;

    public GVFPathFollower(HermitePath path, final Pose initialPose, double kN, double kS, double kC) {
        this.path = path;
        this.currentPose = initialPose;
        this.kN = kN;
        this.kS = kS;
        this.kC = kC;
    }

    public double getNearestT() {
        // Vector2D nearestSplineDist = new Vector2D(0, Integer.MAX_VALUE);

        // for (int i = 0; i < path.length(); i++) {
        //     for (int j = 1; j < 10; j++) {
        //         double currentT = ((double) j / 10) + i;

        //         double dist = currentPose.subt(path.get(currentT, 0)).toVec2D().magnitude();
        //         if (dist < nearestSplineDist.y) {
        //             nearestSplineDist.x = currentT;
        //             nearestSplineDist.y = dist;
        //         }
        //     }
        // } // 50

        // double pGuess = nearestSplineDist.x;
        // for (int i = 0; i < 20; i++) {
        //     Pose tPose = path.get(pGuess, 0);

        //     double ds = (currentPose.subt(tPose).toVec2D().unit()).dot(path.get(pGuess, 1).toVec2D().unit()); 
        //     pGuess = clamp(pGuess + ds * 0.05, 0.0, path.length());

        //     // Vector2D pointVector = currentPose.toVec2D().subt(tPose.subt(currentPose).toVec2D());
        //     // Vector2D tangentVector = path.get(pGuess, 1).toVec2D().unit(); 

        //     // pGuess = clamp(pGuess - tangentVector.dot(pointVector), 0, path.length());
        // } // 5



        // return pGuess; // your dad

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
                nearestSplineDist2.x = currentT;
                nearestSplineDist2.y = dist;
            }
        }

        return nearestSplineDist2.x;
    }

    public Pose calculateGVF() {
        double startTime = System.nanoTime();
        nearestT = getNearestT();
        System.out.println("TIME:" + (System.nanoTime() - startTime) / 1000000000);
        if (nearestT < 1e-2) {
            nearestT = 1e-2;
        }

        Vector2D tangent = path.get(nearestT, 1).toVec2D().unit();
        Vector2D normal = tangent.rotate(Math.PI / 2);
        Pose nearestPose = path.get(nearestT, 0);

        double heading = nearestPose.heading;

        Vector2D displacement = nearestPose.subt(currentPose).toVec2D();
        double error = displacement.magnitude() * Math.signum((displacement.cross(tangent)));
        
        double vMax = MAX_VELOCITY;

        Vector2D gvf = (tangent.subt(normal.mult(kN).mult(error))).unit();

        double decel_disp = currentPose.subt(path.endPose()).toVec2D().magnitude();        
        if (decel_disp < DECEL_PERIOD_DIST) {
            vMax = vMax * (decel_disp / DECEL_PERIOD_DIST); 
        }

        double curvature = path.curvature(nearestT);
        if (curvature != 0) {
            vMax = Math.min(Math.sqrt(MAX_ACCEL / (curvature * kC)), vMax);
        }

        double alpha = 0.9;
        vMax = alpha * lastVelocity + (1 - alpha) * vMax; 

        gvf = gvf.mult(vMax).mult(kS);

        if (nearestT >= path.length() - 1e-2) {
            gvf = displacement.unit().project(gvf);
        }

        lastVelocity = vMax;
        return new Pose(gvf, heading);
    }

    public double errorMap(double error) {
        return Math.max(error, 1);
    }

    public void setCurrentPose(Pose currentPose) {
        this.currentPose = currentPose;
    }

    public boolean isFinished() {
        return currentPose.toVec2D().subt(path.endPose().toVec2D()).magnitude() < FINISH_TOLERANCE;
    }

    public void resetV() {
        lastVelocity = 0.0;
    }

    private double clamp(double num, double min, double max) {
        return Math.max(min, Math.min(num, max));
    }
}
