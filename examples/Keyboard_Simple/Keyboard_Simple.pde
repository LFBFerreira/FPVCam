import space.luisferreira.cam.FpvCam;
import peasy.PeasyCam;

float PANNING_SPEED=3;
        int CAMERA_ZFAR=6000;
        float CAMERA_FOVY=3.0f;

public FpvCam camera;

public Keyboard_Simple(){
        }

// ================================================================

public void settings(){
        size(800,600,P3D);
        }

public void setup(){
        camera=new FpvCam(this,100);
        this.registerMethod("keyEvent",this);
        }

public void draw(){
        background(255);

        //
        camera.updateCamera();

        // fix for clipping when close to surfaces
        // https://discourse.processing.org/t/peasycam-minimum-maximum-distance/6635
        this.perspective(PI/CAMERA_FOVY,(float)width/height,1,CAMERA_ZFAR);

        // draw objects
        lights();
        scale(2);
        noStroke();
        fill(0,255,100);
        circle(0,0,10);
        pushMatrix();
        translate(20,0);
        fill(0,0,255);
        box(10);
        popMatrix();
        }

public void keyEvent(KeyEvent event){

        // Navigation keys
        switch(event.getKey()){
        // normal speed
        case's':
        if(event.getAction()==KeyEvent.PRESS){
        camera.panCameraStart(0,0,PANNING_SPEED,true);
        }else if(event.getAction()==KeyEvent.RELEASE){
        camera.panCameraStop(0,0,PANNING_SPEED);
        }
        break;
        case'w':
        if(event.getAction()==KeyEvent.PRESS){
        camera.panCameraStart(0,0,-PANNING_SPEED,true);
        }else if(event.getAction()==KeyEvent.RELEASE){
        camera.panCameraStop(0,0,-PANNING_SPEED);
        }
        break;
        case'a':
        if(event.getAction()==KeyEvent.PRESS){
        camera.panCameraStart(-PANNING_SPEED,0,0,true);
        }else if(event.getAction()==KeyEvent.RELEASE){
        camera.panCameraStop(-PANNING_SPEED,0,0);
        }
        break;
        case'd':
        if(event.getAction()==KeyEvent.PRESS){
        camera.panCameraStart(PANNING_SPEED,0,0,true);
        }else if(event.getAction()==KeyEvent.RELEASE){
        camera.panCameraStop(PANNING_SPEED,0,0);
        }
        break;
        case'q':
        if(event.getAction()==KeyEvent.PRESS){
        camera.panCameraStart(0,PANNING_SPEED,0,false);
        }else if(event.getAction()==KeyEvent.RELEASE){
        camera.panCameraStop(0,PANNING_SPEED,0);
        }
        break;
        case'e':
        if(event.getAction()==KeyEvent.PRESS){
        camera.panCameraStart(0,-PANNING_SPEED,0,false);
        }else if(event.getAction()==KeyEvent.RELEASE){
        camera.panCameraStop(0,-PANNING_SPEED,0);
        }
        break;
        }
        }
