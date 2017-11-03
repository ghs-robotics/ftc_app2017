import processing.video.*;
Capture cam;
float[][] kern = {{  1,  0, -1}, 
                    {  2,  0, -2}, 
                    {  1,  0, -1}};

void setup() {
  size(1280, 480);
  String[] cameras = Capture.list();
  cam = new Capture(this, cameras[0]);
  cam.start();
  kern = scarr(kern, KERNEL_SCALER);
}

void draw() {
  if(cam.available()) {
    cam.read();
    //int lastMillis = millis();
    float[][] convRes = kcvFloat(cam, kern);
    boolean[] edges = new boolean[convRes.length];
    edges = fcFloat(sumcFloat(convRes));
    drawResults(cam, convRes, edges);
    //if(lastMillis != millis()) println(1000 / (millis() - lastMillis));
  }
}