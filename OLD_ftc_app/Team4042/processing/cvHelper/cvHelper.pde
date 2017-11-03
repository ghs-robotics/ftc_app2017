PImage dispImg;
float zoom = 2;
int initX = 0;
int initY = 0;
int dispX = 0;
int dispY = 0;
int tempDispX = 0;
int tempDispY = 0;
void setup() {
  //size(640, 360);
  size(1280, 720);
  dispImg = loadImage("25.jpg");
}

void draw() {
  if(mousePressed) {
    tempDispX = dispX + intClamp((-mouseX + initX)/((int) (zoom)), 0, (int) (dispImg.width - (dispImg.width/zoom)));
    tempDispY = dispY + (-mouseY + initY)/((int) (zoom));
    println(tempDispX + " " + tempDispY);
  }
  displayImage(dispImg, tempDispX, tempDispY, (int) (zoom));
  
  
}

void mousePressed() {
  initX = mouseX;
  initY = mouseY;
}
void mouseReleased() {
  dispX = tempDispX;
  dispY = tempDispY;
}