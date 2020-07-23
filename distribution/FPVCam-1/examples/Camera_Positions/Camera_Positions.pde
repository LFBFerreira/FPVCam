import space.luisferreira.cam.FpvCam;

public FpvCam camera;

void setup(){
    size(300,200,PConstants.P2D);
    println("Hello world!");
}


void draw(){
    background(frameCount%255);
}