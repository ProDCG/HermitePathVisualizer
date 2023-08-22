package Source;

public class Spline {
    private Polynomial x, y;

    public Spline(Polynomial x, Polynomial y) {
        this.x = x;
        this.y = y;
    }

    public Point calculate(double t, int n) {
        return new Point(x.calculate(t, n), y.calculate(t, n));
    }

    public double getHeading(double t) {
        return Math.atan2(y.calculate(t, 1), x.calculate(t, 1));
    }

}
