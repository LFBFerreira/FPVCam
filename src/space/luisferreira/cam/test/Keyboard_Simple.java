package space.luisferreira.cam.test;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import space.luisferreira.cam.FpvCam;
import peasy.PeasyCam;

public class Keyboard_Simple extends PApplet {
    // movement speed
    float PANNING_SPEED = 3;

    // camera clipping distance
    int CAMERA_ZFAR = 6000;

    // camera field of view
    float CAMERA_FOVY = 3.0f;

    FpvCam camera;


    public void settings() {
        size(800, 600, P3D);
    }

    public void setup() {
        // create a new camera, with a distance of 100 to the focus point
        camera = new FpvCam(this, 100);

        this.registerMethod("keyEvent", this);
    }

    public void draw() {
        background(255);

        // manually update the camera in draw
        camera.updateCamera();

        // fix for clipping when close to surfaces
        // https://discourse.processing.org/t/peasycam-minimum-maximum-distance/6635
        this.perspective(PI / CAMERA_FOVY, (float) width / height, 1, CAMERA_ZFAR);

        scale( -1, 1);

        // draw objects
        lights();
        scale(2);

        drawAxis(g, false, 30, 3);

        noStroke();
        fill(0, 255, 100);
        circle(0, 0, 10);
        pushMatrix();
        translate(20, 0);
        fill(0, 0, 255);
        box(10);
        popMatrix();



        //image(g, -g.width, 0);
    }

    public void keyEvent(KeyEvent event) {
        // Navigation keys
        switch (event.getKey()) {
            case 's':
                if (event.getAction() == KeyEvent.PRESS) {
                    camera.startPan(0, 0, PANNING_SPEED, true);
                } else if (event.getAction() == KeyEvent.RELEASE) {
                    camera.stopPan(0, 0, PANNING_SPEED);
                }
                break;
            case 'w':
                if (event.getAction() == KeyEvent.PRESS) {
                    camera.startPan(0, 0, -PANNING_SPEED, true);
                } else if (event.getAction() == KeyEvent.RELEASE) {
                    camera.stopPan(0, 0, -PANNING_SPEED);
                }
                break;
            case 'a':
                if (event.getAction() == KeyEvent.PRESS) {
                    camera.startPan(-PANNING_SPEED, 0, 0, true);
                } else if (event.getAction() == KeyEvent.RELEASE) {
                    camera.stopPan(-PANNING_SPEED, 0, 0);
                }
                break;
            case 'd':
                if (event.getAction() == KeyEvent.PRESS) {
                    camera.startPan(PANNING_SPEED, 0, 0, true);
                } else if (event.getAction() == KeyEvent.RELEASE) {
                    camera.stopPan(PANNING_SPEED, 0, 0);
                }
                break;
            case 'q':
                if (event.getAction() == KeyEvent.PRESS) {
                    camera.startPan(0, PANNING_SPEED, 0, false);
                } else if (event.getAction() == KeyEvent.RELEASE) {
                    camera.stopPan(0, PANNING_SPEED, 0);
                }
                break;
            case 'e':
                if (event.getAction() == KeyEvent.PRESS) {
                    camera.startPan(0, -PANNING_SPEED, 0, false);
                } else if (event.getAction() == KeyEvent.RELEASE) {
                    camera.stopPan(0, -PANNING_SPEED, 0);
                }
                break;
            case ' ':
                camera.stop();
        }
    }

    private void drawAxis(PGraphics g, Boolean loadBuffer, int axisLength, int axisThickness) {
        if (loadBuffer) {
            g.beginDraw();
        }

        g.pushStyle();

        // X axis, red
        g.strokeWeight(0.5f);
        g.fill(180, 0, 0, 80);
        g.stroke(255, 0, 0);

        g.pushMatrix();
        g.translate(axisLength / 2, 0, 0);
        g.box(axisLength, axisThickness, axisThickness);
        g.popMatrix();

        // Y axis, green
        g.fill(0, 180, 0, 80);
        g.stroke(0, 255, 0);

        g.pushMatrix();
        g.translate(0, axisLength / 2, 0);
        g.box(axisThickness, axisLength, axisThickness);
        g.popMatrix();

        // Z axis, blue
        g.fill(0, 0, 180, 80);
        g.stroke(0, 0, 255);

        g.pushMatrix();
        g.translate(0, 0, axisLength / 2);
        g.box(axisThickness, axisThickness, axisLength);
        g.popMatrix();

        g.popStyle();

        if (loadBuffer) {
            g.endDraw();
        }
    }
}