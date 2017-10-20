PImage dispImg;
float zoom = 2;
int initX = 0;
int initY = 0;
int dispX = 0;
int dispY = 0;
void setup() {
  size(640, 360);
  //size(1280, 720);
  dispImg = loadImage("25.jpg");
}

void draw() {
  println(dispX + " " + dispY);
  delay(100);
  
}

void mousePressed() {
  initX = mouseX;
  initY = mouseY;
}
void mouseReleased() {
  dispX += mouseX - initX;
  dispY += mouseY - initY;
}