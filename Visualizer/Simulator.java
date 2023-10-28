package Visualizer;

import Source.GVFPathFollower;
import Source.HermitePath;
import Source.Pose;
import Source.Vector2D;

public class Simulator {
    public static void main(String[] args) {
        HermitePath trajectory = new HermitePath()
        .addPose(96, 0, new Vector2D(0, 1000))
        .addPose(84, 48, new Vector2D(0, 2500))
        .addPose(84, 96, new Vector2D(0, 500))
        .addPose(72, 108, new Vector2D(250, 0))
        .construct();

    final Pose startingPose = new Pose(30, 30, Math.PI);
    Pose currentPose = startingPose;
    
    GVFPathFollower follower = new GVFPathFollower(
        trajectory, 
        startingPose, 
        0.1, 0.5, 1
        );
    }
}
