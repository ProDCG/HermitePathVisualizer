package Visualizer;

import Source.HermitePath;
import Source.Polynomial;
import Source.Pose;
import Source.Spline;
import Source.Test;

public class GradientTest {

    public static void main(String[] args) {
        // Polynomial x = new Polynomial(1, 0, 8 + 2.0/3.0, -5 - 2.0/3.0);
        // Polynomial y = new Polynomial(1, 0.75, 5, -3.75);
        // Spline s = new Spline(x, y); 
        HermitePath path = new HermitePath()
        .addPose(0, 0, Math.PI / 2, 50)
        .addPose(10, 10, 0, 50)
        .addPose(20, 20, Math.PI / 2, 50)
        .addPose(30, 30, 0, 50)
        .addPose(50, 50, Math.PI / 2, 50)
        .construct();

        Spline s = path.getSpline(0);
        Polynomial x = s.getX();
        Polynomial y = s.getY();
        Spline s2 = path.getSpline(2);
        Polynomial x2 = s2.getX();
        Polynomial y2 = s2.getY();

        System.out.println(path.length());

        for (int i = 0; i < path.length(); i++) {
            Spline a = path.getSpline(i);
            Polynomial a2 = a.getX();
            Polynomial a3 = a.getY();
            Test t3 = new Test(a2, a3, a);
            double curT = t3.findMinimumT(new Pose(40, 40, 0));
            System.out.println(curT + i);
        }

        // Test t = new Test(x, y, s);
        // Test t2 = new Test(x2, y2, s2);
        // double currentTime = System.nanoTime();
        // double a = t.findMinimumT(new Pose(40, 40, 0));
        // System.out.println("TIME SPENT: " + (System.nanoTime() - currentTime) / 1e9 + "ms");
        // System.out.println(a);
        // double b = t2.findMinimumT(new Pose(40, 40, 0));
        // System.out.println("TIME SPENT: " + (System.nanoTime() - currentTime) / 1e9 + "ms");
        // System.out.println(b + 1);
    }
}
