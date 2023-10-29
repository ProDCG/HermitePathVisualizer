package Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Source.HermitePath;

import static org.junit.jupiter.api.Assertions.*;

public class HermitePathAssert {
    HermitePath path;

    @BeforeEach
    public void setup() {
        path = new HermitePath();
    }

    @Test
    public void testAddPoseByCoordinatesAndTangent() {
        path.addPose(10, 20, new Vector2D(0, 1));
        assertEquals(1, path.length());
    }

    @Test
    public void testAddPoseByCoordinatesHeadingAndMagnitude() {
        path.addPose(10, 20, Math.PI, 2);
        assertEquals(1, path.length());
    }

    @Test
    public void testAddPoseByCoordinatesAndHeading() {
        path.addPose(10, 20, Math.PI);
        assertEquals(1, path.length());
    }

    @Test
    public void testConstructionWithFewPoses() {
        path.addPose(0, 0, new Vector2D(1, 0));
        assertThrows(IllegalStateException.class, () -> path.construct());
    }

    @Test
    public void testGetAtStart() {
        path.addPose(0, 0, new Vector2D(1, 0))
            .addPose(10, 10, new Vector2D(0, 1))
            .construct();
        Pose pose = path.get(0, 1);
        assertEquals(0, pose.x);
        assertEquals(0, pose.y);
    }

    @Test
    public void testGetHeading() {
        path.addPose(0, 0, new Vector2D(1, 0))
            .addPose(10, 10, new Vector2D(0, 1))
            .construct();
        double heading = path.getHeading(0.5);
        assertNotNull(heading);
    }

    @Test
    public void testCurvature() {
        path.addPose(0, 0, new Vector2D(1, 0))
            .addPose(10, 10, new Vector2D(0, 1))
            .construct();
        double curvature = path.curvature(0.5);
        assertNotNull(curvature);
    }

    @Test
    public void testGetSpline() {
        path.addPose(0, 0, new Vector2D(1, 0))
            .addPose(10, 10, new Vector2D(0, 1))
            .construct();
        Spline spline = path.getSpline(0.5);
        assertNotNull(spline);
    }

    @Test
    public void testGetSplines() {
        path.addPose(0, 0, new Vector2D(1, 0))
            .addPose(10, 10, new Vector2D(0, 1))
            .construct();
        ArrayList<Spline> splines = path.getSplines();
        assertFalse(splines.isEmpty());
    }

    @Test
    public void testStartEndPoses() {
        path.addPose(0, 0, new Vector2D(1, 0))
            .addPose(10, 10, new Vector2D(0, 1))
            .construct();
        assertEquals(0, path.startPose().x);
        assertEquals(0, path.startPose().y);
        assertEquals(10, path.endPose().x);
        assertEquals(10, path.endPose().y);
    }

    // GVF
    private GVFPathFollower follower;

    @BeforeEach
    public void setUp() {
        testPath = new HermitePath()
                .addPose(0, 0, new Vector2D(1, 0))
                .addPose(10, 10, new Vector2D(0, 1))
                .construct();
        follower = new GVFPathFollower(testPath, new Pose(0, 0, 0), 0.1, 0.5, 1);
    }

    @Test
    public void testProjectPosNewInitialPosition() {
        double projectedPosition = follower.projectPosNew(new Pose(0, 0, 0));
        assertTrue(projectedPosition < 0.1);
    }

    @Test
    public void testCalculateGVFInitialPosition() {
        Pose gvfPose = follower.calculateGVF();
        assertNotNull(gvfPose);
    }

    @Test
    public void testErrorMap() {
        double mappedError = follower.errorMap(0.5);
        assertEquals(1, mappedError, 1e-6);
    }

    @Test
    public void testSetCurrentPose() {
        Pose newPose = new Pose(5, 5, Math.PI);
        follower.setCurrentPose(newPose);
        Pose gvfPose = follower.calculateGVF();
        assertNotNull(gvfPose);
    }

    @Test
    public void testIsFinishedFalse() {
        assertFalse(follower.isFinished());
    }

    @Test
    public void testIsFinishedTrue() {
        follower.setCurrentPose(new Pose(10, 10, Math.PI));
        assertTrue(follower.isFinished());
    }

    @Test
    public void testResetV() {
        follower.calculateGVF();
        follower.resetV();
        Pose gvfPose = follower.calculateGVF(); 
    }

    @BeforeEach
    public void setUp() {
        testPath = new HermitePath()
                .addPose(0, 0, new Vector2D(1, 0))
                .addPose(10, 10, new Vector2D(0, 1))
                .construct();
        follower = new GVFPathFollower(testPath, new Pose(0, 0, 0), 0.1, 0.5, 1);
    }

    @Test
    public void testProjectPosNewInitialPosition() {
        double projectedPosition = follower.projectPosNew(new Pose(0, 0, 0));
        assertTrue(projectedPosition < 0.1);
    }

    @Test
    public void testProjectPosNewMiddlePosition() {
        double projectedPosition = follower.projectPosNew(new Pose(5, 5, Math.PI/4));
        assertTrue(projectedPosition > 0.4 && projectedPosition < 0.6);
    }

    @Test
    public void testProjectPosNewEndPosition() {
        double projectedPosition = follower.projectPosNew(new Pose(10, 10, Math.PI/2));
        assertTrue(projectedPosition > 0.9);
    }

    @Test
    public void testCalculateGVFInitialPosition() {
        Pose gvfPose = follower.calculateGVF();
        assertNotNull(gvfPose);
    }

    @Test
    public void testCalculateGVFMiddlePosition() {
        follower.setCurrentPose(new Pose(5, 5, Math.PI/4));
        Pose gvfPose = follower.calculateGVF();
        assertNotNull(gvfPose);
    }

    @Test
    public void testCalculateGVFEndPosition() {
        follower.setCurrentPose(new Pose(10, 10, Math.PI/2));
        Pose gvfPose = follower.calculateGVF();
        assertNotNull(gvfPose);
    }

    @Test
    public void testErrorMap() {
        double mappedError = follower.errorMap(0.5);
        assertEquals(1, mappedError, 1e-6);
    }

    @Test
    public void testSetCurrentPose() {
        Pose newPose = new Pose(5, 5, Math.PI);
        follower.setCurrentPose(newPose);
        Pose gvfPose = follower.calculateGVF();
        assertNotNull(gvfPose);
    }

    @Test
    public void testIsFinishedFalse() {
        assertFalse(follower.isFinished());
    }

    @Test
    public void testIsFinishedTrue() {
        follower.setCurrentPose(new Pose(10, 10, Math.PI));
        assertTrue(follower.isFinished());
    }

    @Test
    public void testResetV() {
        follower.calculateGVF();
        follower.resetV();
        Pose gvfPose = follower.calculateGVF();
    }

    @Test
    public void testNearestTUpdate() {
        double initialT = GVFPathFollower.nearestT;
        follower.calculateGVF();
        double updatedT = GVFPathFollower.nearestT;
        assertTrue(updatedT != initialT);
    }

    @Test
    public void testBestPosUpdate() {
        Pose initialBestPos = GVFPathFollower.bestPos;
        follower.calculateGVF();
        Pose updatedBestPos = GVFPathFollower.bestPos;
        assertNotEquals(updatedBestPos, initialBestPos);
    }

    @Test
    public void testPathCompleteness() {
        while(!follower.isFinished()) {
            follower.calculateGVF();
            assertFalse(follower.isFinished());
        }
    }

    private Polynomial polyFromCoeffs;
    private Polynomial polyFromMatrix;

    @BeforeEach
    public void setUp() {
        polyFromCoeffs = new Polynomial(1, -2, 3, -4); // 1 - 2x + 3x^2 - 4x^3
        SimpleMatrix matrix = new SimpleMatrix(4, 1, true, 1, -2, 3, -4);
        polyFromMatrix = new Polynomial(matrix);
    }

    @Test
    public void testInitializationFromCoeffs() {
        assertEquals("1.0, -2.0, 3.0, -4.0", polyFromCoeffs.toString());
    }

    @Test
    public void testInitializationFromMatrix() {
        assertEquals("1.0, -2.0, 3.0, -4.0", polyFromMatrix.toString());
    }

    @Test
    public void testCalculateAtZero() {
        assertEquals(1, polyFromCoeffs.calculate(0));
    }

    @Test
    public void testCalculateAtOne() {
        // 1 - 2 + 3 - 4 = -2
        assertEquals(-2, polyFromCoeffs.calculate(1));
    }

    @Test
    public void testCalculateAtTwo() {
        // 1 - 4 + 12 - 32 = -23
        assertEquals(-23, polyFromCoeffs.calculate(2));
    }

    @Test
    public void testFirstDerivativeAtZero() {
        // -2 + 0 + 0 = -2
        assertEquals(-2, polyFromCoeffs.calculate(0, 1));
    }

    @Test
    public void testFirstDerivativeAtOne() {
        // -2 + 6 - 12 = -8
        assertEquals(-8, polyFromCoeffs.calculate(1, 1));
    }

    @Test
    public void testSecondDerivativeAtOne() {
        // 6 - 24 = -18
        assertEquals(-18, polyFromCoeffs.calculate(1, 2));
    }

    @Test
    public void testIllegalNValue() {
        assertThrows(IllegalArgumentException.class, () -> polyFromCoeffs.calculate(1, -1));
        assertThrows(IllegalArgumentException.class, () -> polyFromCoeffs.calculate(1, 5));
    }

    @Test
    public void testErrorMessage() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> polyFromCoeffs.calculate(1, -1));
        assertTrue(e.getMessage().contains("Have you heard the story of darth plageuis the wise?"));
    }

    private Spline spline;

    @BeforeEach
    public void setUp() {
        Polynomial xPoly = new Polynomial(1, -2, 3, -4);  // 1 - 2t + 3t^2 - 4t^3
        Polynomial yPoly = new Polynomial(-1, 2, -3, 4); // -1 + 2t - 3t^2 + 4t^3
        spline = new Spline(xPoly, yPoly);
    }

    @Test
    public void testGetX() {
        assertEquals("1.0, -2.0, 3.0, -4.0", spline.getX().toString());
    }

    @Test
    public void testGetY() {
        assertEquals("-1.0, 2.0, -3.0, 4.0", spline.getY().toString());
    }

    @Test
    public void testCalculatePoseAtZero() {
        Pose result = spline.calculate(0, 0);
        assertEquals(1, result.x);
        assertEquals(-1, result.y);
        assertEquals(0, result.heading);
    }

    @Test
    public void testCalculatePoseAtOne() {
        Pose result = spline.calculate(1, 0);
        assertEquals(-2, result.x); // 1 - 2 + 3 - 4 = -2
        assertEquals(2, result.y);  // -1 + 2 - 3 + 4 = 2
    }

    @Test
    public void testCurvatureAtZero() {
        // Using the curvature formula provided.
        double curvature = spline.curvature(0);
        // Make sure to test for a known or pre-computed value.
        assertEquals(expectedCurvatureValue, curvature, 1e-6);
    }

    @Test
    public void testCurvatureAtOne() {
        // Using the curvature formula provided.
        double curvature = spline.curvature(1);
        // Make sure to test for a known or pre-computed value.
        assertEquals(expectedCurvatureValueAtOne, curvature, 1e-6);
    }

    @Test
    public void testGetHeadingAtZero() {
        double heading = spline.getHeading(0);
        // Use Math.atan2 to get the expected value or use a known value.
        assertEquals(expectedHeadingValueAtZero, heading, 1e-6);
    }

    @Test
    public void testGetHeadingAtOne() {
        double heading = spline.getHeading(1);
        // Use Math.atan2 to get the expected value or use a known value.
        assertEquals(expectedHeadingValueAtOne, heading, 1e-6);
    }
}
