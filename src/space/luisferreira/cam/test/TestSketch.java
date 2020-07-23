package space.luisferreira.cam.test;

import processing.core.PApplet;
import processing.core.PConstants;
import space.luisferreira.cam.FpvCam;

public class TestSketch extends PApplet {

    public FpvCam camera;

    public TestSketch() {
    }

    // ================================================================

    /**
     * Settings Method
     */
    public void settings() {
        size(300, 200, PConstants.P2D);
    }

    /**
     * Setup Method
     */
    public void setup() {
        println("Hello world!");
    }

    public void draw() {
        background(frameCount % 255);
    }
}