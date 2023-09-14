package Source;

class Point {
    public double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() {
        return this.x;
    }

    double getY() {
        return this.y;
    }

    void setX(double x) {
        this.x = x;
    }

    void setY(double y) {
        this.y = y;
    }

    Point getPoint() {
        return this;
    }

    Point subt(Point other) {
        return new Point(getX() - other.getX(), getY() - other.getY());
    }

    Point add(Point other) {
        return new Point(getX() + other.getX(), getY() + other.getY());
    }

    Point add(double scalar) {
        return new Point(x + scalar, y + scalar);
    }

    public String toString() {
        return getX() + ", " + getY();
    }
}