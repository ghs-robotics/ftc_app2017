import processing.video.*;

Capture cam;


float[][] kernel = {{  1,  0, -1}, 
                    {  2,  0, -2}, 
                    {  1,  0, -1}};

PImage convolutedResult;
boolean[] edgeHere;


void setup() {
  size(1280, 480);
  String[] cameras = Capture.list();
  cam = new Capture(this, cameras[0]);
  cam.start();
  kernel = scaleKernel(kernel, KERNEL_SCALER);
}

void draw() {
  if(cam.available()) {
    cam.read();
  }
  
  //runs vertical edge detection kernel convolution
  convolutedResult = kernelConvolution(cam, kernel); 
  
  edgeHere = new boolean[convolutedResult.width];
  edgeHere = filterColumns(convolutedResult);
  
  //draws convolution result and camera input side by side with potential edge columns highlighted
  drawCamKernelConvolutionHighlights(convolutedResult, cam, edgeHere);
}