package Source;

public class GVFPathFollower {
    private HermitePath path;

    private final int MAX_APPROX = 5;
    private final double MAX_SPEED = 10; /* Inches per second */
    private final double MAX_ACCEL = 50; /* Inches per second squared */
    private double kN;
    private double kP;
    private Pose currentPose;

    public GVFPathFollower(HermitePath path, final Pose initialPose, double kN, double kP) {
        this.path = path;
        this.currentPose = initialPose;
        this.kN = kN;
        this.kP = kP;
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
        Vector2D tangent = path.get(getNearestT(), 1).toVec2D().unit();
        Vector2D perp = tangent.rotate(Math.PI / 2);
        


        return null;

    }

    public void setCurrentPose(Pose currentPose) {
        this.currentPose = currentPose;
    }

    private double clamp(double num, double min, double max) {
        return Math.max(min, Math.min(num, max));
    }
}
