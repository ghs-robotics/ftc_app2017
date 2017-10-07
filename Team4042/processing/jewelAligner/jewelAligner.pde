PImage img;
int dist = 35;

void setup() {
  size(1280, 720);
  String imgString = Integer.toString(dist) + ".jpg";
  img = loadImage(imgString);
  kernelScale(kernel, KERNEL_SCALER);
  noLoop();
}

void draw() {
  println(distToPos(dist));
  image(img, 0, 0);
  float[] tapes = findTape(dist, img, kernel);
  line(tapes[0], 0, tapes[0], height - 1);
  line(tapes[1], 0, tapes[1], height - 1);
  println(distToLength(dist));
}