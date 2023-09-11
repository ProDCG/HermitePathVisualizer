package Visualizer;

import Source.Vector2D;

public class Prototyping {
    public static void main(String[] args) {
        Vector2D test = new Vector2D(6, 7);
        System.out.println(test.unit().magnitude());
        System.out.println(test.unit());
    }
}
