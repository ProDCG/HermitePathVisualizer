package Visualizer;

import Source.HermiteInterpolator;
import Source.HermitePath;
import Source.HermitePose;
import Source.Vector2D;

public class Prototyping {
    public static void main(String[] args) {
        HermitePose startPose = new HermitePose(0.0, 0.0, new Vector2D(250.0, 0.0));
        HermitePose endPose = new HermitePose(-60.0, 0.0, new Vector2D(500.0, 0.0));
        HermitePath path = new HermitePath()
        .addPose(startPose)
        .addPose(endPose)
        .construct();

        System.out.println(path.get(0, 234243));
    }
}
