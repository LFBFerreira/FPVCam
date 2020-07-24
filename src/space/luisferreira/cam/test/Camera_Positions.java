package space.luisferreira.cam.test;

import processing.core.PApplet;
import processing.event.KeyEvent;
import space.luisferreira.cam.FpvCam;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Camera_Positions extends PApplet {
    String CAMERA_STATES_FILE = "cameras.json";

    // camera clipping distance
    int CAMERA_ZFAR = 6000;

    // camera field of view
    float CAMERA_FOVY = 3.0f;

    public FpvCam camera;


    public void settings() {
        size(800, 600, P3D);
    }

    public void setup() {
        camera = new FpvCam(this, 100);
        this.registerMethod("keyEvent", this);
    }

    public void draw() {
        background(255);

        // update camera position
        camera.updateCamera();

        // fix for clipping when close to surfaces
        // https://discourse.processing.org/t/peasycam-minimum-maximum-distance/6635
        this.perspective(PI / CAMERA_FOVY, (float) width / height, 1, CAMERA_ZFAR);

        // draw objects
        lights();
        scale(2);
        noStroke();
        fill(0, 255, 100);
        circle(0, 0,  10);
        pushMatrix();
        translate(20, 0);
        fill(0, 0, 255);
        box(10);
        popMatrix();
    }

    public void keyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.RELEASE) {
            switch (event.getKey()) {
                case 's':
                    camera.saveCameraState();
                    println("Camera state saved");
                    break;
                case 'd':
                    camera.deleteCameraState();
                    println("Camera state deleted");
                    break;
                case 'f':
                    saveCameraStatesToFile();
                    break;
                case '+':
                    camera.goToNextState(500);
                    println("Camera state " + camera.getStateIndex());
                    break;
                case '-':
                    camera.goToPreviousState(500);
                    println("Camera state " + camera.getStateIndex());
                    break;
            }
        }
    }


    void saveCameraStatesToFile() {
        // get the path to the resource, root is the sketch path
        Path filePath = Paths.get(sketchPath(CAMERA_STATES_FILE));
        println("Saving camera states to " + filePath.toAbsolutePath());
        camera.saveCameraStatesToFile(filePath);
    }
}
