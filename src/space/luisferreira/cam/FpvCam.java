package space.luisferreira.cam;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import peasy.PeasyCam;
import peasy.CameraState;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;


public class FpvCam extends PeasyCam {

    private PApplet parent;
    private List<CameraState> cameraStates = new ArrayList<>();
    private int cameraIdx = 0;
    private PVector newLookAt = new PVector();
    private boolean panLockY = false;
    private boolean isCameraPanning = false;
    private boolean cameraIndexChanged = false;
    private PVector cameraPanDirection = new PVector();
    private int transitionSpeed = 0;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     * @param parent PAplet
     * @param distance distance to focus point
     */
    public FpvCam(PApplet parent, int distance) {
        super(parent, distance);
        this.parent = parent;
    }


    /**
     * Processing's dispose function, frees all resources
     */
    public void dispose() {
    }

    // -----------------------------------------------------------------------------------------------------------------

    // public methods

    /**
     * Start panning in the provided direction
     *
     * @param x
     * @param y
     * @param z
     */
    public void startPan(float x, float y, float z) {
        startPan(x, y, z, panLockY);
    }

    /**
     * Start panning in the provided direction
     *
     * @param x
     * @param y
     * @param z
     * @param lockY true to lock movement in the Y axis
     */
    public void startPan(float x, float y, float z, boolean lockY) {
        cameraPanDirection.add(x, y, z);
        isCameraPanning = true;
        panLockY = lockY;
    }

    /**
     * Stop panning in the provided direction
     *
     * @param x
     * @param y
     * @param z
     */
    public void stopPan(float x, float y, float z) {
        stopPan(x, y, z, panLockY);
    }

    /**
     * Stop panning in the provided direction
     *
     * @param x
     * @param y
     * @param z
     * @param lockY true to lock movement in the Y axis
     */
    public void stopPan(float x, float y, float z, boolean lockY) {
        cameraPanDirection.sub(x, y, z);
        panLockY = lockY;
        if (cameraPanDirection.equals(new PVector(0, 0, 0))) {
            isCameraPanning = false;
        }
    }

    /**
     * Stop panning in any direction
     */
    public void stop() {
        cameraPanDirection = new PVector();
        isCameraPanning = false;
    }

    /**
     * Update the camera position
     */
    public void updateCamera() {
        if (cameraIndexChanged && cameraStates.size() > cameraIdx) {
            setState(cameraStates.get(cameraIdx), transitionSpeed);
            cameraIndexChanged = false;
            stop();
        }

        if (isCameraPanning) {
            panCamera(cameraPanDirection);
        }

    }

    // -----------------------------------------------------------------------------------------------------------------

    // Camera States

    /**
     * Save the current camera state
     */
    public void saveCameraState()
    {
        cameraStates.add(getState());
    }

    /**
     * Save camera states to a JSON file
     * @param filePath
     */
    public void saveCameraStatesToFile(Path filePath) {
        File saveFile = filePath.toFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonData = gson.toJson(cameraStates);

        try (FileWriter file = new FileWriter(saveFile)) {
            file.write(jsonData);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads camera states from a JSON file
     * @param filePath
     */
    public void loadCameraStates(Path filePath) {
        File file = filePath.toFile();

        if (!file.exists()) {
            System.out.println("Could not find the camera states file");
            return;
        }

        System.out.println("Loading camera states from " + file.getAbsolutePath());

        String jsonArray = "";

        try {
            jsonArray = Files.readString(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<ArrayList<CameraState>>() {
        }.getType();
        List<CameraState> loadedStates = new Gson().fromJson(jsonArray, listType);

        if (!loadedStates.isEmpty()) {
            cameraStates = new ArrayList<>(loadedStates);
        }
    }

    /**
     *
     */
    public void deleteCameraState() {
        if (cameraIdx >= cameraStates.size() || cameraStates.isEmpty()) {
            return;
        }

        System.out.println("Deleting camera state " + cameraIdx);

        cameraStates.remove(cameraIdx);
    }

    /**
     * Go to the next camera state
     * @param speed frames
     */
    public void goToNextState(int speed) {
        int newIndex = cameraIdx + 1;

        if (newIndex >= cameraStates.size()) {
            newIndex = 0;
        }

        goToState(newIndex, speed);
    }

    /**
     * Go to the previous camera state
     * @param speed frames
     */
    public void goToPreviousState(int speed) {
        int newIndex = cameraIdx - 1;

        if (newIndex < 0) {
            newIndex = cameraStates.size() - 1;
        }

        goToState(newIndex, speed);
    }

    /**
     * Go to a specific camera state
     * @param index camera state index
     * @param speed frames
     */
    public void goToState(int index, int speed) {
        if (cameraStates.isEmpty()) {
            System.out.println("There are no saved cameras");
            return;
        }

        if (index < 0 || index >= cameraStates.size()) {
            System.out.println("Invalid camera index");
            return;
        }

        cameraIdx = index;
        transitionSpeed = speed;
        cameraIndexChanged = true;
        System.out.println("Switching to camera " + (cameraIdx + 1));
    }


    /**
     * Get the camera state index
     * @return
     */
    public int getStateIndex()
    {
        return cameraIdx;
    }

    /**
     * Reset the camera's roll. Not working
     */
    public void resetCameraRoll() {
        float[] rotations = getRotations(); // x, y, and z rotations required to face camera in model space
//        camera.setRotations(0, rotations[1], rotations[2]); // rotations are applied in that order

        float[] state = getRotations();
        // reset roll
        PVector result = rotateZ(new PVector(state[0], state[1], state[2]), state[2]);

        setRotations(result.x, result.y, result.z);
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Helpers

    /**
     * Pan camera in the given direction
     * @param direction
     */
    private void panCamera(PVector direction) {
        // calculates the necessary rotation to math the desired direction with the camera rotation
        float[] rotations = getRotations();
        PVector adjustedMove = rotateXYZ(direction, rotations[0], -rotations[1], -rotations[2]);

        float[] lookPoint = getLookAt();
        PVector center = new PVector(lookPoint[0], lookPoint[1], lookPoint[2]);

        // calculate the camera's new look-at point
        center = center.add(adjustedMove);

        // limit movement in Y or not
        if (panLockY) {
            newLookAt = new PVector(center.x, lookPoint[1], center.z);
        } else {
            newLookAt = new PVector(lookPoint[0], center.y, lookPoint[2]);
        }

        // Peasy lookAt call
        lookAt(newLookAt.x, newLookAt.y, newLookAt.z, 0);
    }

    /**
     * Rotates a vector in 3D space
     * https://discourse.processing.org/t/pvector-rotate-use-for-3d/10958
     *
     * @param vector
     * @param angleX
     * @param angleY
     * @param angleZ
     * @return
     */
    private PVector rotateXYZ(PVector vector, float angleX, float angleY, float angleZ) {
        PVector result = vector.copy();
        result = rotateX(result, angleX);
        result = rotateY(result, angleY);
        return rotateZ(result, angleZ);
    }

    /**
     * Rotates a 2D vector over the X axis
     * https://discourse.processing.org/t/pvector-rotate-use-for-3d/10958
     *
     * @param vec
     * @param angle
     * @return
     */
    private PVector rotateX(PVector vec, float angle) {
        PVector atx = new PVector(vec.y, vec.z);
        atx.rotate(angle);
        atx.set(vec.x, atx.x, atx.y);
        atx.sub(vec);
        return vec.add(atx);
    }

    /**
     * Rotates a 2D vector over the Y axis
     * https://discourse.processing.org/t/pvector-rotate-use-for-3d/10958
     *
     * @param vec
     * @param angle
     * @return
     */
    private PVector rotateY(PVector vec, float angle) {
        PVector aty = new PVector(vec.x, vec.z);
        aty.rotate(angle);
        aty.set(aty.x, vec.y, aty.y);
        aty.sub(vec);
        return PVector.add(vec, aty);
    }

    /**
     * Rotates a 2D vector over the Z axis
     * https://discourse.processing.org/t/pvector-rotate-use-for-3d/10958
     *
     * @param vec
     * @param angle
     * @return
     */
    private PVector rotateZ(PVector vec, float angle) {
        PVector atz = new PVector(vec.x, vec.y);
        atz.rotate(angle);
        atz.set(atz.x, atz.y, vec.z);
        atz.sub(vec);
        return PVector.add(vec, atz);
    }
}

