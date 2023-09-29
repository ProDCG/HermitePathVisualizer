package Visualizer;

import Source.Polynomial;
import Source.Pose;
import Source.Spline;
import Source.Test;

public class GradientTest {
    

    public static void main(String[] args) {
        Polynomial x = new Polynomial(1, 0, 8 + 2.0/3.0, -5 - 2.0/3.0);
        Polynomial y = new Polynomial(1, 0.75, 5, -3.75);
        Spline s = new Spline(x, y);  

        Test t = new Test(x, y, s);
        double currentTime = System.nanoTime();
        double a = t.findT(new Pose(3.53, 1.8, 0));
        System.out.println("TIME SPENT: " + (System.nanoTime() - currentTime) / 1e9 + "ms");
    }
}
