package Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;

public class GVFPathFollower {
    private HermitePath path;
    // private ArrayList<Vector2D> distances;
    // private Vector2D[] subDistances = new Vector2D[3];

    private final double MAX_VELOCITY = 180; /* Inches per second */
    private final double MAX_ACCEL = 480; /* Inches per second squared */
    private final double MAX_DECEL = 720; /* Inches per second squared */
    private final double FINISH_TOLERANCE = 0.1; /* Finishing Error */
    private double lastVelocity = 0.5;

    private final double DECEL_PERIOD_DIST = (Math.pow(MAX_VELOCITY, 2)) / (2 * MAX_DECEL);

    private double kN;
    private double kS;
    private double kC;
    private Pose currentPose;
    public static double nearestT = 0.0;

    private final double TOLERANCE = 1e-6;
    private final int MAX_ITERATIONS = 10;

    public GVFPathFollower(HermitePath path, final Pose initialPose, double kN, double kS, double kC) {
        this.path = path;
        this.currentPose = initialPose;
        this.kN = kN;
        this.kS = kS;
        this.kC = kC;
        
        // this.distances = new ArrayList<>();
    }

    // private double gradientOf(Spline s, double t) {
    //     double dx = s.getX().calculate(t, 1);
    //     double dy = s.getY().calculate(t, 1);
    //     double fx = s.getX().calculate(t, 0) - currentPose.x;
    //     double fy = s.getY().calculate(t, 0) - currentPose.y;
    //     return 2 * (dx * fx + dy * fy);
    // }

    // public Vector2D findMinimumT(Spline currentSpline) {
    //     double currentT = 0.5;
    //     for (int i = 0; i < MAX_ITERATIONS; i++) {
    //         double gradient = gradientOf(currentSpline, currentT);
    //         double hessian = 2 * (Math.pow(currentSpline.getX().calculate(currentT, 1), 2) + Math.pow(currentSpline.getY().calculate(currentT, 1), 1));
    //         double newT = currentT - (gradient / hessian);

    //         if (Math.abs(newT - currentT) < TOLERANCE) {
    //             break;
    //         }
    //         currentT = newT;
    //     }

    //     currentT = MathUtils.clamp(currentT, 0.0, 1.0);
    //     Vector2D dist1 = new Vector2D(0.0, dist(currentSpline, 0.0));
    //     Vector2D dist2 = new Vector2D(currentT, dist(currentSpline, currentT));
    //     Vector2D dist3 = new Vector2D(1.0 - 1e-6, dist(currentSpline, 1.0 - 1e-6));

    //     Vector2D nearestDist = Arrays.stream(new Vector2D[]{dist1, dist2, dist3})
    //                                 .min(Comparator.comparing(Vector2D::getY))
    //                                 .orElse(null);

    //     // double distance = (currentSpline.calculate(currentT, 0).subt(currentPose)).toVec2D().magnitude();
    //     // System.out.println(currentT);
    //     return new Vector2D(nearestDist.x, nearestDist.y);
    // }

    // private double dist(Spline s, double currentT) {
    //     return (s.calculate(currentT, 0).subt(currentPose)).toVec2D().magnitude();
    // }

    // public double gradientOf(Spline s, double t) {
    //     double dx = s.getX().calculate(t, 1);
    //     double dy = s.getY().calculate(t, 1);
    //     double fx = s.getX().calculate(t, 0) - currentPose.x;
    //     double fy = s.getY().calculate(t, 0) - currentPose.y;
    //     return 2 * (dx * fx + dy * fy);
    // }

    // public Vector2D findMinimumT(Spline s) {
    //     double currentT = 0.5;
    //     for (int i = 0; i < MAX_ITERATIONS; i++) {
    //         double gradient = gradientOf(s, currentT);
    //         double hessian = 2 * (Math.pow(s.getX().calculate(currentT, 1), 2) + Math.pow(s.getY().calculate(currentT, 1), 1));
    //         double newT = currentT - (gradient / hessian);

    //         if (Math.abs(newT - currentT) < 1e-6) {
    //             break;
    //         }
    //         currentT = newT;
    //     }

    //     currentT = MathUtils.clamp(currentT, 0.0, 1.0);
    //     Vector2D dist1 = new Vector2D(0.0, dist(s, 0.0));
    //     Vector2D dist2 = new Vector2D(currentT, dist(s, currentT));
    //     Vector2D dist3 = new Vector2D(1.0 - 1e-6, dist(s, 1.0 - 1e-6));

    //     Vector2D[] distances = {dist1, dist2, dist3};

    //     Vector2D nearestDist = Arrays.stream(distances)
    //                                 .min(Comparator.comparing(Vector2D::getY))
    //                                 .orElse(null);
    //     return nearestDist;
    // }

    // private double dist(Spline s, double currentT) {
    //     return (s.calculate(currentT, 0).subt(currentPose)).toVec2D().magnitude();
    // }

    // public double getNearestT() {
    //     Vector2D overallNearest = new Vector2D(0, Double.MAX_VALUE);
    //     int splineCount = path.length() - 1;

    //     for (int i = 0; i < splineCount; i++) {
    //         Spline currentSpline = path.getSpline(i);
    //         Vector2D currentSplineNearest = findMinimumT(currentSpline);

    //         if (currentSplineNearest.y < overallNearest.y) {
    //             overallNearest = currentSplineNearest;
    //             overallNearest.x += i;
    //         }
    //     }

    //     // return MathUtils.clamp(overallNearest.x, 0.0, splineCount + 1);
    //     return overallNearest.x;
    // }
    
    // public double gradientOf(Spline s, double t) {
    //     double dx = s.getX().calculate(t, 1);
    //     double dy = s.getY().calculate(t, 1);
    //     double fx = s.getX().calculate(t, 0) - currentPose.x;
    //     double fy = s.getY().calculate(t, 0) - currentPose.y;
    //     return 2 * (dx * fx + dy * fy);
    // }

    // public Vector2D findMinimumT(Spline s) {
    //     double currentT = 0.5;
    //     for (int i = 0; i < 10; i++) {
    //         double gradient = gradientOf(s, currentT);
    //         double hessian = 2 * (Math.pow(s.getX().calculate(currentT, 1), 2) + Math.pow(s.getY().calculate(currentT, 1), 1));
    //         double newT = currentT - (gradient / hessian);

    //         if (Math.abs(newT - currentT) < 1e-6) {
    //             break;
    //         }
    //         currentT = newT;
    //     }

    //     currentT = MathUtils.clamp(currentT, 0.0, 1.0);
    //     subDistances[0] = new Vector2D(0.0, dist(s, 0.0));
    //     subDistances[1] = new Vector2D(currentT, dist(s, currentT));
    //     subDistances[2] = new Vector2D(1.0 - 1e-6, dist(s, 1.0 - 1e-6));

    //     return Arrays.stream(subDistances)
    //     .min(Comparator.comparing(Vector2D::getY))
    //     .orElse(null);
    // }

    // private double dist(Spline s, double t) {
    //     return (s.calculate(t, 0).subt(currentPose)).toVec2D().magnitude();
    // }

    // public double getNearestT() {
    //     distances.clear();
    //     for (int i = 0; i < path.length(); i++) {
    //         Spline currentSpline = path.getSpline(i);
    //         Vector2D current = findMinimumT(currentSpline);
    //         current.x += i;
    //         System.out.println(current);
    //         distances.add(current);
    //     }

    //     return distances.stream()
    //     .min(Comparator.comparing(Vector2D::getY))
    //     .orElse(null).getX();
    // }

    // public double gradientOf(Spline s, double t) {
    //     double dx = s.getX().calculate(t, 1);
    //     double dy = s.getY().calculate(t, 1);
    //     double fx = s.getX().calculate(t, 0) - currentPose.x;
    //     double fy = s.getY().calculate(t, 0) - currentPose.y;
    //     return 2 * (dx * fx + dy * fy);
    // }

    // public Vector2D findMinimumT(Spline s) {
    //     double currentT = 0.5;
    //     for (int i = 0; i < 10; i++) {
    //         double gradient = gradientOf(s, currentT);
    //         double hessian = 2 * (Math.pow(s.getX().calculate(currentT, 1), 2) + Math.pow(s.getY().calculate(currentT, 1), 1));
    //         double newT = currentT - (gradient / hessian);

    //         if (Math.abs(newT - currentT) < 1e-6) {
    //             break;
    //         }
    //         currentT = newT;
    //     }

    //     currentT = MathUtils.clamp(currentT, 0.0, 1.0);
    //     Vector2D dist1 = new Vector2D(0.0, dist(s, 0.0));
    //     Vector2D dist2 = new Vector2D(currentT, dist(s, currentT));
    //     Vector2D dist3 = new Vector2D(1.0 - 1e-6, dist(s, 1.0 - 1e-6));

    //     Vector2D[] distances = {dist1, dist2, dist3};

    //     Vector2D nearestDist = Arrays.stream(distances)
    //                                 .min(Comparator.comparing(Vector2D::getY))
    //                                 .orElse(null);

    //     // double distance = (s.calculate(currentT, 0).subt(currentPose)).toVec2D().magnitude();
    //     return nearestDist;
    // }



    private double dist(Spline s, double currentT) {
        return (s.calculate(currentT, 0).subt(currentPose)).toVec2D().magnitude();
    }

    // public double getNearestT() {
    //     ArrayList<Vector2D> distances = new ArrayList<>();
    //     for (int i = 0; i < path.length(); i++) {
    //         Spline a = path.getSpline(i + 1e-7);
    //         Vector2D curT = findMinimumT(a);
    //         curT.x += i;
    //         distances.add(curT);
    //     }

    //     System.out.println(distances.toString());

    //     return distances.stream()
    //     .min(Comparator.comparing(Vector2D::getY))
    //     .orElse(null).getX();
    // }

    private double projectPos(Spline s, Pose position) {
        double curr = 0.5;
        double length = 0.0;

        for (double t = 0.01; t <= 1; t += 1) {
            Pose prev = s.calculate(t - 0.01, 0);
            Pose now = s.calculate(t, 0);
            length += prev.subt(now).toVec2D().magnitude();
        }

        for (int i = 0; i < 250; i++) {
            Pose p = s.calculate(curr, 0);
            Pose deriv = s.calculate(curr, 1);

            double ds = position.subt(p).toVec2D().dot(deriv.toVec2D());

            ds = ds / deriv.toVec2D().dot(deriv.toVec2D());

            if (MathUtils.epsilonEquals(ds, 0.0)) {
                break;
            }

            curr += (ds / length);

            if (curr < 0) {
                curr = 0;
            }
            if (curr > 1) {
                curr = 1;
            }

        }

        return (Math.max(0, Math.min(curr, 0)));
    }

    public double projectPosNew(Pose position) {
        double minDist = Double.MAX_VALUE;
        Pose bestPos = new Pose(0, 0, 0);
        double projectPos = 0;

        for (Spline s : path.getSplines()) {
            double t = projectPos(s, position);
            Pose pos = path.get(t, 0);
            if (pos.subt(position).toVec2D().magnitude() < minDist) {
                minDist = pos.subt(position).toVec2D().magnitude();
                bestPos = pos;
                projectPos = t;
            }
        }

        return projectPos;

    }

    public Pose calculateGVF() {
        double startTime = System.nanoTime();
        nearestT = projectPosNew(currentPose);
        System.out.println("T" + nearestT + " TIME:" + (System.nanoTime() - startTime) / 1000000000);
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

    
}
