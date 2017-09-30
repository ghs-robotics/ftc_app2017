import processing.video.*;

Capture cam;


float[][] kernel = {{  1,  0, -1}, 
                    {  2,  0, -2}, 
                    {  1,  0, -1}};

PImage convolutedResult;
boolean[] edgeHere;

int time;
int lastFPSUpdate = 0;
int frames = 0;
int lastFrames = 0;
int camTime;
int convolutionTime;
int edgeTime;
int drawTime;

int extraReturnMax;

void setup() {
  size(1280, 480);
  String[] cameras = Capture.list();
  cam = new Capture(this, cameras[0]);
  cam.start();
  kernel = scaleKernel(kernel, KERNEL_SCALER);
}

void draw() {
  time = millis();
  camTime = time;
  if(cam.available()) {
    cam.read();
  }
  camTime = millis() - camTime;
  
  convolutionTime = millis();
  //runs vertical edge detection kernel convolution
  convolutedResult = kernelConvolution(cam, kernel); 
  convolutionTime = millis() - convolutionTime;
  
  edgeTime = millis();
  edgeHere = new boolean[convolutedResult.width];
  edgeHere = filterColumns(convolutedResult);
  edgeTime = millis() - edgeTime;
  
  PImage newConvolution = edgeLengths(edgeHere, convolutedResult, cam);
  
  //draws convolution result and camera input side by side with potential edge columns highlighted
  drawTime = millis();
  drawCamKernelConvolutionHighlights(convolutedResult, newConvolution, edgeHere);
  drawTime = millis() - drawTime;
  frames++;
  text(lastFrames, 10, 10);
  text(camTime, 10, 30);
  text(convolutionTime, 10, 50);
  text(edgeTime, 10, 70);
  text(drawTime, 10, 90);
  text(extraReturnMax, 10, 110);
  if((time - lastFPSUpdate) > 1000){
    lastFPSUpdate = time;
    lastFrames = frames;
    frames = 0;
  }
}