PImage img;
int dist = 50;

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
  float[] tapes = findTape(dist, img, kernel);
  line(tapes[0], 0, tapes[0], height - 1);
  line(tapes[1], 0, tapes[1], height - 1);
  println(getLeftColor(temp, (tapes[1] + tapes[0]) / 2, dist, 10, COLOR_CONTRAST_FACTOR));
  println(distToLength(dist));
  println((tapes[1] + tapes[0]) / 2);
  println(millis() - start);
}