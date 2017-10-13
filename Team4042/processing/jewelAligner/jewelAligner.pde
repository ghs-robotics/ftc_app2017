PImage img;
int dist = 24;

void setup() {
  size(1280, 720);
  //size(300, 200);
  String imgString = Integer.toString(dist) + ".jpg";
  img = loadImage(imgString);
  kernelScale(kernel, KERNEL_SCALER);
  noLoop();
}

void draw() {
  float start = millis();
  println(distToPos(dist));
  image(img, 0, 0);
  PImage temp = img.get(0, 0, img.width, img.height);
  float[] tapes = {0, 0};
  int fudge = 0;
  for(fudge = 0; fudge <= 4; fudge++) {
    tapes = findTape(dist + fudge, img, kernel);
    if(tapes[0] != 0 || tapes[1] != 0) {
      break;
    }
    tapes = findTape(dist - fudge, img, kernel);
    if(tapes[0] != 0 || tapes[1] != 0) {
      fudge = -1 * fudge;
      break;
    }
    
  }
  line(tapes[0], 0, tapes[0], height - 1);
  line(tapes[1], 0, tapes[1], height - 1);
  line(0, distToPos(dist + fudge), width - 1, distToPos(dist + fudge));
  
  println(getLeftColor(temp, (tapes[1] + tapes[0]) / 2, dist + fudge, 10, COLOR_CONTRAST_FACTOR));
  println(distToLength(dist + fudge));
  println((tapes[1] + tapes[0]) / 2);
  println(millis() - start);
}