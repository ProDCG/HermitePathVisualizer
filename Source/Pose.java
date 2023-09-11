package Source;

public class Pose {
    public double x, y, heading;

    public Pose(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public Pose(Vector2D vec, double heading) {
        this.x = vec.x;
        this.y = vec.y;
        this.heading = heading;
    }

    public Pose add(Pose other) {
        return new Pose(x + other.x, y + other.y, heading + other.heading);
    }

    public Pose subt(Pose other) {
        return new Pose(x - other.x, y - other.y, heading - other.heading);
    }

    public Vector2D toVec2D() {
        return new Vector2D(x, y);
    }

    public String toString() {
        return String.format("%.2f, %.2f, %.2f)", x, y, heading);
    }
}
